/**
 * 
 */
package my.chat.db;

import my.chat.exceptions.ChatException;

/**
 * @author taras.kovalchuk
 *
 */
public class ChatLoginException extends ChatException {

	/**
	 * @param message
	 */
	public ChatLoginException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ChatLoginException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

}
