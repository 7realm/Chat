/**
 * 
 */
package my.chat.db;

import my.chat.exceptions.ChatException;

/**
 * 
 * <p>
 * <b>Thread safe:</b> No.
 *
 * @author 7realm
 */
public class PersistanceChatException extends ChatException {
    private static final long serialVersionUID = 6636253929959354207L;

    public PersistanceChatException(String message, Object... params) {
        super(message, params);
    }

    public PersistanceChatException(String message, Throwable cause, Object... params) {
        super(message, cause, params);
    }
}
