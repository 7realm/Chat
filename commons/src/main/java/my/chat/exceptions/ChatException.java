package my.chat.exceptions;

import static my.chat.commons.Helper.makeMessage;

public class ChatException extends Exception {
    private static final long serialVersionUID = -3738433909980832208L;

    public ChatException(String message, Object... params) {
        super(makeMessage(message, params));
    }

    public ChatException(String message, Throwable cause, Object... params) {
        super(makeMessage(message, params), cause);
    }
}
