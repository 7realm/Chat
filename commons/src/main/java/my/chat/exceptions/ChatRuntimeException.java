/**
 * 
 */
package my.chat.exceptions;

/**
 * Base runtime exception for all exceptions in this application.
 * <p>
 * <b>Thread safe:</b> No.
 *
 * @author 7realm
 */
public class ChatRuntimeException extends RuntimeException {

    /**  */
    private static final long serialVersionUID = 4765530370566519581L;
    
    /**
     * @param message
     */
    public ChatRuntimeException(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     * @param cause
     */
    public ChatRuntimeException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

   

}
