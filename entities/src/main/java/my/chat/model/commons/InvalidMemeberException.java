/**
 * 
 */
package my.chat.model.commons;

import my.chat.exceptions.ChatRuntimeException;


/**
 * Exception that will occur if some class member of Model entity is invalid.
 * <p>
 * <b>Thread safe:</b> No.
 *
 * @author 7realm
 */
public class InvalidMemeberException extends ChatRuntimeException {

    public InvalidMemeberException(String message) {
        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public InvalidMemeberException(String message, Throwable cause) {
        super(message, cause);
    }

}