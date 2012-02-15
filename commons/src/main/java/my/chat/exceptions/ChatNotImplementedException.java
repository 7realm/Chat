package my.chat.exceptions;

public class ChatNotImplementedException extends ChatRuntimeException {
    private static final long serialVersionUID = 3274976826969538912L;

    public ChatNotImplementedException(String message, Object... params) {
        super(message, params);
    }

    public ChatNotImplementedException(String message, Throwable cause, Object... params) {
        super(message, cause, params);
    }
}
