/**
 * 
 */
package my.chat.server;

import my.chat.exceptions.ChatException;

/**
 * This exception is used to indicate failure during command processing.
 * <p>
 * This exception is package private because it will be used only by {@link CommandProcessor}.
 * <p>
 * <b>Thread safe:</b> No.
 *
 * @author 7realm
 */
class CommandProcessorChatException extends ChatException {
    private static final long serialVersionUID = -8443551071878387789L;

    /**
     * @param message
     * @param params
     */
    public CommandProcessorChatException(String message, Object... params) {
        super(message, params);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     * @param cause
     * @param params
     */
    public CommandProcessorChatException(String message, Throwable cause, Object... params) {
        super(message, cause, params);
        // TODO Auto-generated constructor stub
    }

}
