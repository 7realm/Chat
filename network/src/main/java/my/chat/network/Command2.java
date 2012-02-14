package my.chat.network;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Command2 {
    private static AtomicInteger lastId = new AtomicInteger(0);
    
    private final long id;
    private final String version;
    private final CommandType type;
    private final List<CommandData> data;
    
    public Command2(CommandType type) {
        this.type = type;

        id = lastId.getAndIncrement();
        version = NetworkConfig.COMMAND_VERSION;
        data = new ArrayList<CommandData>();
    }
    
    public Command2(long id, String version, CommandType type) {
        this.id = id;
        this.version = version;
        this.type = type;
        
        data = new ArrayList<CommandData>();
    }

    public Command2 addItem(String name, Object value) {
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
        /** For user login. */
        LOGIN,

        /** For user enter to chat. */
        USER_ENTER,

        /** For user exit from chat. */
        USER_EXIT,

        /** For private message between users. */
        USER_MESSAGE,

        /** This command is sent when user successfully connected. */
        CONNECTED,

        /** User entered channel. */
        CHANNEL_JOIN,

        /** User left channel. */
        CHANNEL_LEAVE,

        /** For public message in chat channel. */
        CHANNEL_MESSAGE
    }
}