/**
 * 
 */
package my.chat.client.console;

/**
 * 
 * <p>
 * <b>Thread safe:</b> No.
 * 
 * @author 7realm
 */
public class Instruction {
    private final InstructionType type;
    private final Object[] parameters;

    private Instruction(InstructionType type, Object... parameters) {
        this.type = type;
        this.parameters = parameters;
    }

    public static Instruction parse(String line) throws InstructionChatException {
        if (line == null) {
            throw new InstructionChatException("Null line is passed.");
        }

        // apply defaults
        line = applyDefaults(line);

        if (line.startsWith("!")) {
            for (InstructionType type : InstructionType.values()) {
                if (type.match(line)) {
                    Parameter[] parameterTypes = type.getParameters();
                    int paramCount = parameterTypes.length;

                    // split by space to parameters
                    String[] parts = line.split(" ", parameterTypes.length + 1);
                    if (parts.length - 1 != parameterTypes.length) {
                        throw new InstructionChatException("Incorrect parameter count, expected %1 but present %2 at command %3.",
                            paramCount, parts.length - 1, type);
                    }

                    // convert parameters
                    Object[] params = new Object[parameterTypes.length];
                    for (int i = 0; i < params.length; i++) {
                        switch (parameterTypes[i]) {
                        case STRING:
                            params[i] = parts[i + 1];
                            break;
                        case INT:
                            try {
                                params[i] = Integer.parseInt(parts[i + 1]);
                            } catch (NumberFormatException e) {
                                throw new InstructionChatException("Paramter %1 '%2' cannot be parsed to integer.", i + 1, parts[i + 1]);
                            }
                            break;
                        default:
                            throw new InstructionChatException("Unsupported paramter %1.", parameterTypes[i]);
                        }
                    }

                    return new Instruction(type, params);
                }
            }

            throw new InstructionChatException("Unsupported command name.");
        } else {
            // chat message
            return new Instruction(InstructionType.SEND_CHAT, line);
        }
    }

    private static String applyDefaults(String line) {
        if (line.indexOf(" ") == -1) {
            if (InstructionType.CONNECT.match(line)) {
                line = line + " " + ConfigManager.get(ConfigManager.PROP_SERVER_HOST) + " "
                    + ConfigManager.getInt(ConfigManager.PROP_SERVER_PORT);
            } else if (InstructionType.LOGIN.match(line)) {
                line = line + " " + ConfigManager.get(ConfigManager.PROP_DEFAULT_USER_NAME) + " "
                    + ConfigManager.get(ConfigManager.PROP_DEFAULT_USER_PASSWORD);
            } else if (InstructionType.JOIN.match(line)) {
                line = line + " " + ConfigManager.get(ConfigManager.PROP_DEFAULT_CHANNEL);
            }
        }
        return line;
    }

    public InstructionType getType() {
        return type;
    }

    public String getParam(int i) {
        return (String) parameters[i];
    }

    public int getIntParam(int i) {
        return (Integer) parameters[i];
    }

    public enum Parameter {
        STRING("str"),
        INT("int");
        
        private final String info;
        
        Parameter(String info) {
            this.info = info;
        }
        
        public String getInfo() {
            return info;
        }
    }

    public enum InstructionType {
        CONNECT("!connect", "!c", "connect to server, 1st param is host, 2nd is port, default values are configured", Parameter.STRING, Parameter.INT),
        LOGIN("!login", "!l", "login to chat, 1st param is username, 2nd is password, default values are configured", Parameter.STRING, Parameter.STRING),

        JOIN("!join", "!j", "join channel, 1st param is channel name, default values are configured", Parameter.STRING),
        EXIT("!exit", "!e",  "leave current channel"),

        ADD_CONTACT("!add", "!a", "add user to contacts, 1st params is username", Parameter.STRING),
        REMOVE_CONTACT("!remove", "!r", "remove user from contacts, 1st params is username", Parameter.STRING),

        SEND("!send", "!s", "send message to user, 1st param is username, 2nd is message", Parameter.STRING, Parameter.STRING),
        SEND_CHAT("!send_chat", "!sc", "send message to current chat, 1st param is message; can be omitted.", Parameter.STRING),

        QUIT("!quit", "!q", "quits the client"),
        
        HELP("!help", "!h", "help command, prints info");
        
        
        private final String commandLine0;
        private final String commandLine1;
        private final String description;

        private final Parameter[] parameters;

        InstructionType(String commandLine0, String commandLine1, String description, Parameter... parameters) {
            this.commandLine0 = commandLine0;
            this.commandLine1 = commandLine1;
            this.description = description;
            this.parameters = parameters;
        }

        /**
         * @return the commandLine0
         */
        public String getCommandLine0() {
            return commandLine0;
        }

        /**
         * @return the commandLine1
         */
        public String getCommandLine1() {
            return commandLine1;
        }

        /**
         * @return the parameters
         */
        public Parameter[] getParameters() {
            return parameters;
        }
        
        public String getDescription() {
            return description;
        }

        public boolean match(String line) {
            // TODO differ !sc and !s
            if (line.startsWith(commandLine0) || line.startsWith(commandLine1)) {
                return true;
            }

            return false;
        }
    }
}
