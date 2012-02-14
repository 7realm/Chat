package my.chat.exceptions;

public class ChatNotImplementedException extends ChatRuntimeException {
    private static final long serialVersionUID = 3274976826969538912L;

    public ChatNotImplementedException(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    public ChatNotImplementedException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }
}
