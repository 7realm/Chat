package my.chat.exceptions;

public class ChatException extends Exception {
	private static final long serialVersionUID = -3738433909980832208L;

	public ChatException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}
	
	public ChatException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}
}
