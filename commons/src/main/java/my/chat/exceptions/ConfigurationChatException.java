/**
 * 
 */
package my.chat.exceptions;

/**
 * @author taras.kovalchuk
 *
 */
public class ConfigurationChatException extends ChatRuntimeException {
	private static final long serialVersionUID = 3682597926849607751L;
	
	public ConfigurationChatException(String message, Object... params) {
		super(message, params);
	}

	public ConfigurationChatException(String message, Throwable cause, Object... params) {
		super(message, cause, params);
	}
}
