/**
 * 
 */
package my.chat.client.console;

import my.chat.exceptions.ChatException;

/**
 * 
 * <p>
 * <b>Thread safe:</b> No.
 *
 * @author 7realm
 */
public class InstructionChatException extends ChatException {
    private static final long serialVersionUID = 5058442895356619936L;

    /**
     * @param message
     * @param params
     */
    public InstructionChatException(String message, Object... params) {
        super(message, params);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     * @param cause
     * @param params
     */
    public InstructionChatException(String message, Throwable cause, Object... params) {
        super(message, cause, params);
        // TODO Auto-generated constructor stub
    }

}
