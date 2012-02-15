package my.chat.exceptions;

public class ChatIOException extends ChatException {
	private static final long serialVersionUID = 6406506482947602627L;

	public ChatIOException(String message, Object... params) {
		super(message, params);
	}

	public ChatIOException(String message, Throwable cause, Object... params) {
		super(message, cause, params);
	}
}
