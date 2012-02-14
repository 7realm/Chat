/**
 * 
 */
package my.chat.db;

import my.chat.exceptions.ChatException;

/**
 * @author taras.kovalchuk
 *
 */
public class LoginChatException extends ChatException {
    private static final long serialVersionUID = 6636253929959354207L;

    /**
	 * @param message
	 */
	public LoginChatException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public LoginChatException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

}
