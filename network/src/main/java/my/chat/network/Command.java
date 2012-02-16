package my.chat.network;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static my.chat.commons.ArgumentHelper.checkString;

public class Command {
    private static AtomicInteger lastId = new AtomicInteger(0);

    private final long id;
    private final String version;
    private final CommandType type;
    private final List<CommandData> data;

    public Command(CommandType type) {
        this.type = type;

        id = lastId.getAndIncrement();
        version = NetworkConfig.COMMAND_VERSION;
        data = new ArrayList<CommandData>();
    }

    public Command(long id, String version, CommandType type) {
        this.id = id;
        this.version = version;
        this.type = type;

        data = new ArrayList<CommandData>();
    }

    public Command addItem(String name, Object value) {
        data.add(new CommandData(name, value));
        return this;
    }

    public long getId() {
        return id;
    }

    public String getVersion() {
        return version;
    }

    public CommandType getType() {
        return type;
    }

    public Iterator<CommandData> iterator() {
        return data.iterator();
    }

    public boolean contains(String name) {
        for (CommandData commandData : data) {
            if (name.equals(commandData.getName())) {
                return true;
            }
        }
        return false;
    }

    public Object get(String name) throws CommandContentException {
        checkString("name", name);

        for (CommandData commandData : data) {
            if (name.equals(commandData.getName())) {
                return commandData.getValue();
            }
        }

        // no need to log error here
        throw new CommandContentException("Command '%1' does not have '%2' data.", type, name);
    }

    public String getString(String name) throws CommandContentException {
        Object value = get(name);
        if (value instanceof String) {
            return (String) value;
        }

        throw new CommandContentException("Command '%1' data item '%2' is not string.", type, name);
    }

    public long getLong(String name) throws CommandContentException {
        Object value = get(name);
        if (value instanceof Long) {
            return (Long) value;
        }

        throw new CommandContentException("Command '%1' data item '%2' is not long.", type, name);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(type.name());
        builder.append(". Data item names: ");
        for (CommandData item : data) {
            builder.append(item.getName());
        }
        return builder.toString();
    }

    /**
     * The command data item.
     * <p>
     * <b>Thread safe:</b> No.
     * 
     * @author 7realm
     */
    public static class CommandData {
        /** The name of data item. */
        private String name;
        /** The data item value. */
        private Object value;

        /**
         * Constructor for command data using fields.
         * 
         * @param name
         * @param value
         */
        private CommandData(String name, Object value) {
            super();
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public Object getValue() {
            return value;
        }
    }

    /**
     * The enumeration of command types.
     * <p>
     * <b>Thread safe:</b> Yes.
     * 
     * @author 7realm
     */
    public static enum CommandType {
        /** Command for creating new account. */
        CREATE,
        
        /** For user login. */
        LOGIN,
        
        /** For user enter to chat. */
        USER_ENTER,

        /** For user exit from chat. */
        USER_EXIT,

        /** For private message between users. */
        USER_MESSAGE,

        /** Request to add user to contacts. */
        USER_ADD_CONTACT,

        /** Request to remove user from contacts. */
        USER_REMOVE_CONTACT,

        /** This command is sent when user successfully connected. */
        LOGGED_IN,

        /** User entered channel. */
        CHANNEL_JOIN,

        /** User left channel. */
        CHANNEL_LEAVE,

        /** For public message in chat channel. */
        CHANNEL_MESSAGE,

        /** General failure message. */
        FAILURE,
    }
}
