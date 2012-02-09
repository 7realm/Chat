/**
 * 
 */
package my.chat.model;

import java.util.Date;

/**
 * Private peer-to-peer communication message between users.
 * <p>
 * <b>Thread safe:</b> No.
 * 
 * @author 7realm
 */
public class PrivateMessage extends Message {
	/** Serial version UID. */
	private static final long serialVersionUID = -5752887916079093451L;
	
	/** The message recipient. */
	private User recipient;

	/**
	 * @param messageId
	 * @param authorId
	 * @param authorDate
	 * @param content
	 * @param replyTo
	 * @param recipient 
	 */
	public PrivateMessage(long messageId, User author, Date authorDate, String content, Message replyTo, User recipient) {
		super(messageId, author, authorDate, content, replyTo);

		this.recipient = recipient;
	}

	/**
	 * @return the recipient
	 */
	public User getRecipient() {
		return recipient;
	}
}
