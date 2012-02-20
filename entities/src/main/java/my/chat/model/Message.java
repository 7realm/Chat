package my.chat.model;

import java.io.Serializable;
import java.util.Date;

import my.chat.model.user.User;

/**
 *  Communication message.
 */
public abstract class Message implements Serializable {
	/** Serial version UID.  */
	private static final long serialVersionUID = -7642358797930262146L;
	
	/** The message ID, unique to user(author ID). */
	private long messageId;
	
	/** The message author. */
	private User author;

	/** The Date of message creation on author's side. */
	private Date authorDate;
	
	/** The Date when message arrived to server. */
	private Date serverDate;
	
	/** The message content. */
	private String content;
	
	/** The message, to which message is reply. Can be null if this is new message. */
	private Message replyTo;
	
	protected Message() {
	    
	}

	/**
	 * @param messageId
	 * @param authorId
	 * @param authorDate
	 * @param content
	 * @param replyTo
	 */
	protected Message(long messageId, User author, Date authorDate, String content, Message replyTo) {
		this.messageId = messageId;
		this.author = author;
		this.authorDate = authorDate;
		this.content = content;
		this.replyTo = replyTo;
	}

	/**
	 * @return the messageId
	 */
	public long getMessageId() {
		return messageId;
	}

	/**
	 * @return
	 */
	public User getAuthor() {
		return author;
	}
	
	public void setAuthor(User author) {
        this.author = author;
    }
	
	/**
	 * @return the authorDate
	 */
	public Date getAuthorDate() {
		return authorDate;
	}

	/**
	 * @return the serverDate
	 */
	public Date getServerDate() {
		return serverDate;
	}

	/**
	 * @param serverDate the serverDate to set
	 */
	public void setServerDate(Date serverDate) {
		this.serverDate = serverDate;
	}

	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * @param content the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * @return the replyTo
	 */
	public Message getReplyTo() {
		return replyTo;
	}
}
