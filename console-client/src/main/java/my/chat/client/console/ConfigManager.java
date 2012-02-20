/**
 * 
 */
package my.chat.client.console;

import java.io.IOException;
import java.util.Properties;

import my.chat.commons.Helper;
import my.chat.exceptions.ConfigurationChatException;

/**
 * Configuration manager.
 * <p>
 * <b>Thread safe:</b> No.
 * 
 * @author 7realm
 */
public class ConfigManager {
    private static final String CONF_FILE_PATH = "conf/chat.properties";

    public static final String PROP_SERVER_PORT = "server.port";
    public static final String PROP_SERVER_HOST = "server.host";

    public static final String PROP_DEFAULT_USER_PASSWORD = "default.user.password";
    public static final String PROP_DEFAULT_USER_NAME = "default.user.name";
    public static final String PROP_DEFAULT_CHANNEL = "default.channel";
    public static final String PROP_WAIT_TIMEOUT = "responce.wait.timeout";

    public static final String PROP_AUTO_CONNECT = "auto.connect";
    public static final String PROP_AUTO_LOGIN = "auto.login";
    public static final String PROP_AUTO_JOIN = "auto.join.main";

    private static final Properties properties;
    private static final Properties defaults;

    static {
        defaults = new Properties();
        defaults.setProperty(PROP_SERVER_HOST, "localhost");
        defaults.setProperty(PROP_SERVER_PORT, "8844");

        defaults.setProperty(PROP_DEFAULT_CHANNEL, "main");
        defaults.setProperty(PROP_DEFAULT_USER_NAME, "ivern");
        defaults.setProperty(PROP_DEFAULT_USER_PASSWORD, "secret");

        defaults.setProperty(PROP_AUTO_CONNECT, "false");
        defaults.setProperty(PROP_AUTO_LOGIN, "false");
        defaults.setProperty(PROP_AUTO_JOIN, "false");

        defaults.setProperty(PROP_WAIT_TIMEOUT, "1000");

        properties = initProperties();

        properties.list(System.out);
    }

    private static Properties initProperties() {
        try {
            return Helper.loadProperties(CONF_FILE_PATH, defaults);
        } catch (IOException e) {
            e.printStackTrace();
            return defaults;
        }
    }

    public static String get(String name) {
        if (properties.getProperty(name) == null) {
            throw new ConfigurationChatException("Missing property %1.", name);
        }
        return properties.getProperty(name);
    }

    public static int getInt(String name) {
        String value = get(name);
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new ConfigurationChatException("Failed to parse %1 value of property %2 to Integer.", value, name);
        }
    }

    public static boolean is(String name) {
        String value = get(name);
        if ("true".equalsIgnoreCase(value)) {
            return true;
        } else if ("false".equalsIgnoreCase(value)) {
            return false;
        } else {
            throw new ConfigurationChatException("Failed to parse %1 value of property %2 to Boolean.", value, name);
        }
    }
}
