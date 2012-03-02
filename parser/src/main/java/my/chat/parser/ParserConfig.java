package my.chat.parser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import my.chat.commands.CommandType;
import my.chat.model.messages.PrivateMessage;
import my.chat.model.user.User;

public final class ParserConfig {
    public static final Map<CommandType, Map<String, Class<?>>> RULES = new HashMap<CommandType, Map<String, Class<?>>>();
    public static final String CHARSET = "UTF-8";

    static {
        // LOGIN
        addRule(CommandType.LOGIN, "username", String.class);
        addRule(CommandType.LOGIN, "password", String.class);
        
        // CONNECTED
        addRule(CommandType.LOGGED_IN, "user", User.class);
        addRule(CommandType.LOGGED_IN, "offlineMessages", List.class);
        addRule(CommandType.LOGGED_IN, "publicChannels", List.class);
        
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
