/**
 * 
 */
package my.chat.exceptions;

import static my.chat.commons.Helper.makeMessage;

/**
 * Base runtime exception for all exceptions in this application.
 * <p>
 * <b>Thread safe:</b> No.
 *
 * @author 7realm
 */
public class ChatRuntimeException extends RuntimeException {
    private static final long serialVersionUID = 4765530370566519581L;
    
    public ChatRuntimeException(String message, Object... params) {
        super(makeMessage(message, params));
    }

    public ChatRuntimeException(String message, Throwable cause, Object... params) {
        super(makeMessage(message, params), cause);
    }
}
