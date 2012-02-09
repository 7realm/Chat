package my.chat.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Chat channel, it will contain users and messages.
 * <p>
 * <b>Thread safe:</b> No.
 * 
 * @author 7realm
 */
public class Channel implements Serializable {
    /** Serial version UID. */
    private static final long serialVersionUID = 7851489666306197186L;

    /** The unique channel ID. */
    private long channelId;

    /** The channel name. */
    private String name;

    /** The channel create date. */
    private Date createDate;

    /** The list of users, present in channel. */
    private List<User> users;

    /** The list of messages, posted at channel. */
    private List<ChatMessage> messages;

    /**
     * @param channelId
     * @param name
     * @param createDate
     */
    public Channel(long channelId, String name, Date createDate) {
        this.channelId = channelId;
        this.name = name;
        this.createDate = createDate;
    }

    /**
     * @return the channelId
     */
    public long getChannelId() {
        return channelId;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the createDate
     */
    public Date getCreateDate() {
        return createDate;
    }

    /**
     * @return the users
     */
    public List<User> getUsers() {
        return users;
    }

    /**
     * @return the messages
     */
    public List<ChatMessage> getMessages() {
        return messages;
    }
}
