package my.chat.parser;

import my.chat.exceptions.ChatException;

public class ParserChatException extends ChatException {
    private static final long serialVersionUID = 6754495985030110514L;

    /**
     * @param message
     */
    public ParserChatException(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     * @param cause
     */
    public ParserChatException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

}
