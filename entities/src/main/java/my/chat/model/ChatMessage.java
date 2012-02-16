/**
 * 
 */
package my.chat.model;

import java.util.Date;

import my.chat.parser.ObjectData;

/**
 * Public communication message to channel.
 * <p>
 * <b>Thread safe:</b> No.
 * 
 * @author 7realm
 */
@ObjectData
public class ChatMessage extends Message {
	/** Serial version UID. */
	private static final long serialVersionUID = 7949962399207029490L;

	/** The channel ID, where this message is sent. */
	private Channel channel;
	
	public ChatMessage() {
	    
	}

	/**
	 * @param messageId
	 * @param authorId
	 * @param authorDate
	 * @param content
	 * @param replyTo
	 * @param channelId
	 */
	public ChatMessage(long messageId, User author, Date authorDate, String content, Message replyTo, Channel channel) {
		super(messageId, author, authorDate, content, replyTo);
		
		this.channel = channel;
	}

	public Channel getChannel() {
		return channel;
	}
}
