/**
 * 
 */
package my.chat.exceptions;

/**
 * @author taras.kovalchuk
 *
 */
public class ChatConfigurationException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3682597926849607751L;
	
	public ChatConfigurationException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public ChatConfigurationException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}
}
