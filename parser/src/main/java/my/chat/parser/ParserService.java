/**
 * 
 */
package my.chat.parser;

import static my.chat.commons.ArgumentHelper.checkNotNull;
import static my.chat.commons.Helper.close;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import my.chat.commons.Helper;
import my.chat.exceptions.ConfigurationChatException;
import my.chat.logging.Log;
import my.chat.model.User;
import my.chat.network.Command;
import my.chat.network.Command.CommandData;
import my.chat.network.Command.CommandType;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * 
 * <p>
 * <b>Thread safe:</b> No.
 * 
 * @author 7realm
 */
public final class ParserService {
    private static final String NODE_LIST = "List";
    private static final String NODE_OBJECT = "Object";
    private static final String NODE_BOOL = "Boolean";
    private static final String NODE_LONG = "Long";
    private static final String NODE_INTEGER = "Integer";
    private static final String NODE_STRING = "String";

    private static final ParserService INSTANCE = new ParserService();
    private Transformer transformer;
    private DocumentBuilder documentBuilder;

    public static final ParserService getInstance() {
        return INSTANCE;
    }

    private ParserService() {
        // empty
    }

    public void start() {
        try {
            // create XML transformer
            TransformerFactory factory = TransformerFactory.newInstance();
            transformer = factory.newTransformer();

            // create XML document builder
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
        } catch (TransformerConfigurationException e) {
            throw Log.error(this, new ConfigurationChatException("Failed to create XML transformer.", e));
        } catch (ParserConfigurationException e) {
            throw Log.error(this, new ConfigurationChatException("Failed to create XML document builder.", e));
        }

        // TODO scan files for rules
    }

    public byte[] marshall(Command command) throws ParserChatException {
        checkNotNull("command", command);

        try {
            Document xmlDoc = documentBuilder.newDocument();
            Element xmlCommand = xmlDoc.createElement("Command");
            xmlDoc.appendChild(xmlCommand);

            // set command header
            setAttribute(xmlCommand, "id", command.getId());
            setAttribute(xmlCommand, "version", command.getVersion());
            setAttribute(xmlCommand, "type", command.getType());

            // set command data
            for (Iterator<CommandData> i = command.iterator(); i.hasNext();) {
                CommandData data = i.next();

                Element item = marshall(xmlDoc, data.getName(), data.getValue());
                xmlCommand.appendChild(item);
            }

            // write XML to string
            StringWriter writer = new StringWriter();
            try {
                Result result = new StreamResult(writer);
                Source source = new DOMSource(xmlDoc);
                transformer.transform(source, result);
                return writer.toString().getBytes(ParserConfig.CHARSET);
            } catch (TransformerException e) {
                throw Log.error(this, new ParserChatException("Failed to write XML document to string. Command: %1.", e, command));
            } catch (UnsupportedEncodingException e) {
                throw Log.error(this, new ParserChatException("Unsupported encoding %1. Command: %2.", e, ParserConfig.CHARSET, command));
            } finally {
                Helper.close(writer);
            }
        } catch (DOMException e) {
            throw Log.error(this, new ParserChatException("Failed to create DOM three.", e));
        }
    }

    private Element marshall(Document xmlDoc, String name, Object value) throws ParserChatException {
        Element result;
        if (value instanceof String) {
            result = xmlDoc.createElement(NODE_STRING);
            setAttribute(result, "value", value);
        } else if (value instanceof Integer) {
            result = xmlDoc.createElement(NODE_INTEGER);
            setAttribute(result, "value", value);
        } else if (value instanceof Long) {
            result = xmlDoc.createElement(NODE_LONG);
            setAttribute(result, "value", value);
        } else if (value instanceof Boolean) {
            result = xmlDoc.createElement(NODE_BOOL);
            setAttribute(result, "value", value);
        } else if (value instanceof List) {
            List<?> list = (List<?>) value;

            // create List node and add items to it
            result = xmlDoc.createElement(NODE_LIST);
            int i = 0;
            for (Object listItem : list) {
                Element xmlListItem = marshall(xmlDoc, name + "[" + i + "]", listItem);
                result.appendChild(xmlListItem);
            }
        } else {
            // use object with reflections
            Class<? extends Object> clazz = value.getClass();
            User.class.isAnnotationPresent(ObjectData.class);
            if (!clazz.isAnnotationPresent(ObjectData.class)) {
                throw Log.error(this, new ParserChatException("Class '%1' named '%2' is missing annotation.", clazz.getName(), name));
            }

            // create Object node and add attribute to it
            result = xmlDoc.createElement(NODE_OBJECT);
            setAttribute(result, "class", clazz.getName());

            // process all annotated fields
            for (Field field : clazz.getDeclaredFields()) {
                if (!field.isAnnotationPresent(FieldDataIgnore.class) && !Modifier.isStatic(field.getModifiers())) {
                    field.setAccessible(true);

                    try {
                        String fieldName = field.getName();
                        Object fieldValue = field.get(value);

                        // add field element
                        Element xmlField = marshall(xmlDoc, fieldName, fieldValue);
                        result.appendChild(xmlField);
                    } catch (IllegalArgumentException e) {
                        Log.warn(this, e, "Unexpected exception.");
                    } catch (IllegalAccessException e) {
                        Log.warn(this, e, "Unexpected exception.");
                    }
                }
            }
        }

        // set name attribute for every node
        setAttribute(result, "name", name);
        return result;
    }

    private static void setAttribute(Element xml, String name, Object value) {
        xml.setAttribute(name, String.valueOf(value));
    }

    public Command unmarshall(byte[] bytes) throws ParserChatException {
        checkNotNull("bytes", bytes);

        // create input source
        String xml;
        try {
            xml = new String(bytes, ParserConfig.CHARSET);

            Log.debug(this, "Received XML: %1.", xml);
        } catch (UnsupportedEncodingException e) {
            throw Log.error(this, new ParserChatException("Unsupported encoding %1.", e, ParserConfig.CHARSET));
        }
        StringReader input = new StringReader(xml);
        try {
            // parse XML to string
            Document xmlDoc = documentBuilder.parse(new InputSource(input));

            // parse header attributes
            Element xmlCommand = xmlDoc.getDocumentElement();
            long id = getLong(xmlCommand, "id");
            String version = getString(xmlCommand, "version");
            CommandType type = getCommandType(xmlCommand, "type");

            // create command
            Command command = new Command(id, version, type);

            NodeList childNodes = xmlCommand.getChildNodes();
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node node = childNodes.item(i);

                // convert each element node to command
                if (node instanceof Element) {
                    Element element = (Element) node;
                    String name = getString(element, "name");
                    Object value = unmarshall(element);
                    command.addItem(name, value);
                }
            }

            return command;
        } catch (SAXException e) {
            throw Log.error(this, new ParserChatException("Failed to parse XML from string '%1'.", e, xml));
        } catch (IOException e) {
            throw Log.error(this, new ParserChatException("Failed to read XML from string '%1'.", e, xml));
        } finally {
            close(input);
        }
    }

    private Object unmarshall(Element xml) throws ParserChatException {
        String tagName = xml.getTagName();

        Object result;
        if (NODE_STRING.equals(tagName)) {
            result = getString(xml, "value");
        } else if (NODE_INTEGER.equals(tagName)) {
            result = getInteger(xml, "value");
        } else if (NODE_LONG.equals(tagName)) {
            result = getLong(xml, "value");
        } else if (NODE_BOOL.equals(tagName)) {
            result = getBoolean(xml, "value");
        } else if (NODE_LIST.equals(tagName)) {
            List<Object> list = new ArrayList<Object>();

            NodeList childNodes = xml.getChildNodes();
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node node = childNodes.item(i);

                // convert each element node to object and add to list
                if (node instanceof Element) {
                    Object value = unmarshall((Element) node);
                    list.add(value);
                }
            }

            // return list
            result = list;
        } else if (NODE_OBJECT.equals(tagName)) {
            String className = getString(xml, "class");
            try {
                Class<?> clazz = Class.forName(className);
                result = clazz.newInstance();

                NodeList childNodes = xml.getChildNodes();
                for (int i = 0; i < childNodes.getLength(); i++) {
                    Node node = childNodes.item(i);

                    // convert each element node to field value
                    if (node instanceof Element) {
                        Element element = (Element) node;
                        Object value = unmarshall(element);
                        String name = getString(element, "name");

                        // set field value
                        Field field = clazz.getDeclaredField(name);
                        field.setAccessible(true);
                        field.set(result, value);
                    }
                }
            } catch (NoSuchFieldException e) {
                throw Log.error(this, new ParserChatException("Failed to find field for class '%1'.", e, className));
            } catch (IllegalArgumentException e) {
                throw Log.error(this, new ParserChatException("Failed to set field for class '%1'.", e, className));
            } catch (IllegalAccessException e) {
                throw Log.error(this, new ParserChatException("Default constructor is not accessible for '%1'.", e, className));
            } catch (InstantiationException e) {
                throw Log.error(this, new ParserChatException("Failed to create class '%1', passed in xml.", e, className));
            } catch (ClassNotFoundException e) {
                throw Log.error(this, new ParserChatException("Failed to find class '%1', passed in xml.", e, className));
            }
        } else {
            throw Log.error(this, new ParserChatException("Unknown tag name '%1'.", tagName));
        }

        return result;
    }

    private static long getLong(Element xml, String name) throws ParserChatException {
        try {
            String value = getString(xml, name);
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            throw Log.error(null, new ParserChatException("Failed to parse long value at '%1'.", e, name));
        }
    }

    private static int getInteger(Element xml, String name) throws ParserChatException {
        try {
            String value = getString(xml, name);
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw Log.error(null, new ParserChatException("Failed to parse int value at '%1'.", e, name));
        }
    }

    private static boolean getBoolean(Element xml, String name) throws ParserChatException {
        String value = getString(xml, name);
        if ("true".equalsIgnoreCase(value)) {
            return true;
        } else if ("false".equalsIgnoreCase(value)) {
            return false;
        } else {
            throw Log.error(null, new ParserChatException("Failed to parse boolean value '%1' at '%2'.", value, name));
        }
    }

    private static CommandType getCommandType(Element xml, String name) throws ParserChatException {
        try {
            String value = getString(xml, name);
            return CommandType.valueOf(value);
        } catch (IllegalArgumentException e) {
            throw Log.error(null, new ParserChatException("Failed to parse command type value at %1'.", e, name));
        }
    }

    private static String getString(Element xml, String name) throws ParserChatException {
        if (!xml.hasAttribute(name)) {
            throw Log.error(null, new ParserChatException("The attribute '%1' is missing.", name));
        }
        return xml.getAttribute(name);
    }

}
