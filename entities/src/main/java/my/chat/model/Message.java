package my.chat.model;

import java.util.Date;

import my.chat.model.commons.ChatEntity;
import my.chat.model.commons.ChatIdEntity;
import my.chat.model.commons.UpdateChatException;
import my.chat.model.user.User;

/**
 * Communication message.
 * <p>
 * The message ID, unique to user(author ID).
 * <p>
 * <b>Thread safe:</b> No.
 * 
 * @author 7realm
 */
public abstract class Message extends ChatIdEntity {
    private static final long serialVersionUID = -7642358797930262146L;

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
        this.author = author;
        this.authorDate = authorDate;
        this.content = content;
        this.replyTo = replyTo;
        
        setId(messageId);
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Date getAuthorDate() {
        return authorDate;
    }
    
    public void setAuthorDate(Date authorDate) {
        this.authorDate = authorDate;
    }

    public Date getServerDate() {
        return serverDate;
    }
    
    public void setServerDate(Date serverDate) {
        this.serverDate = serverDate;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Message getReplyTo() {
        return replyTo;
    }
    
    public void setReplyTo(Message replyTo) {
        this.replyTo = replyTo;
    }
    
    @Override
    public void update(ChatEntity newEntity) throws UpdateChatException {
        super.update(newEntity);
        
        Message newMessage = (Message) newEntity;
        
        setAuthor(newMessage.getAuthor());
        setAuthorDate(newMessage.getAuthorDate());
        setServerDate(newMessage.getServerDate());
        setContent(newMessage.getContent());
        setReplyTo(newMessage.getReplyTo());
    }
}
