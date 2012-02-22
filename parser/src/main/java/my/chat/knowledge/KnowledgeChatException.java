/**
 * 
 */
package my.chat.knowledge;

import my.chat.exceptions.ChatException;

/**
 * 
 * <p>
 * <b>Thread safe:</b> No.
 *
 * @author 7realm
 */
public class KnowledgeChatException extends ChatException {
    private static final long serialVersionUID = -7514951287171374678L;

    /**
     * @param message
     * @param params
     */
    public KnowledgeChatException(String message, Object... params) {
        super(message, params);
    }

    /**
     * @param message
     * @param cause
     * @param params
     */
    public KnowledgeChatException(String message, Throwable cause, Object... params) {
        super(message, cause, params);
    }
}
