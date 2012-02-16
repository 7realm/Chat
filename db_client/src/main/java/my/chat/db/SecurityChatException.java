/**
 * 
 */
package my.chat.db;


/**
 * 
 * <p>
 * <b>Thread safe:</b> No.
 *
 * @author 7realm
 */
public class SecurityChatException extends PersistanceChatException {
    private static final long serialVersionUID = 6636253929959354207L;

    public SecurityChatException(String message, Object... params) {
        super(message, params);
    }

    public SecurityChatException(String message, Throwable cause, Object... params) {
        super(message, cause, params);
    }
}
