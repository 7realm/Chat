package my.chat.parser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import my.chat.model.PrivateMessage;
import my.chat.model.User;
import my.chat.network.Command.CommandType;

public final class ParserConfig {
    public static final Map<CommandType, Map<String, Class<?>>> RULES = new HashMap<CommandType, Map<String, Class<?>>>();
    public static final String CHARSET = "UTF-8";

    static {
        // LOGIN
        addRule(CommandType.LOGIN, "username", String.class);
        addRule(CommandType.LOGIN, "password", String.class);
        
        // CONNECTED
        addRule(CommandType.CONNECTED, "user", User.class);
        addRule(CommandType.CONNECTED, "offlineMessages", List.class);
        addRule(CommandType.CONNECTED, "publicChannels", List.class);
        
        // USER
        addRule(CommandType.USER_ENTER, "user", User.class);
        addRule(CommandType.USER_EXIT, "userId", Long.class);
        addRule(CommandType.USER_MESSAGE, "message", PrivateMessage.class);
    }

    private static void addRule(CommandType commandType, String elementName, Class<?> clazz) {
        if (!RULES.containsKey(commandType)) {
            RULES.put(commandType, new HashMap<String, Class<?>>());
        }

        RULES.get(commandType).put(elementName, clazz);
    }
}