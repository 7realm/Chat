/**
 * 
 */
package my.chat.network.command;

import my.chat.exceptions.ChatException;

/**
 * This exception is thrown if command content is not correct.
 * <p>
 * <b>Thread safe:</b> No.
 *
 * @author 7realm
 */
public class CommandContentException extends ChatException {
    private static final long serialVersionUID = -3427046594014033673L;

    /**
     * @param message
     * @param params
     */
    public CommandContentException(String message, Object... params) {
        super(message, params);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     * @param cause
     * @param params
     */
    public CommandContentException(String message, Throwable cause, Object... params) {
        super(message, cause, params);
        // TODO Auto-generated constructor stub
    }

}
