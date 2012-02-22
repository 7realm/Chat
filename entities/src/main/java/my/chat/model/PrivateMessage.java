/**
 * 
 */
package my.chat.model;

import java.util.Date;

import my.chat.model.commons.ChatEntity;
import my.chat.model.commons.UpdateChatException;
import my.chat.model.user.User;

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

	public PrivateMessage() {

	}
	
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

	public User getRecipient() {
		return recipient;
	}
	
	public void setRecipient(User recipient) {
        this.recipient = recipient;
    }
	
	@Override
	public void update(ChatEntity newEntity) throws UpdateChatException {
	    super.update(newEntity);
	    
	    PrivateMessage newMessage = (PrivateMessage) newEntity;
	    
	    setRecipient(newMessage.getRecipient());
	}
}
