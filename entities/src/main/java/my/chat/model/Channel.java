package my.chat.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import my.chat.model.commons.ChatIdEntity;
import my.chat.model.user.User;
import my.chat.parser.ObjectData;

/**
 * Chat channel, it will contain users and messages.
 * <p>
 * <b>Thread safe:</b> No.
 * 
 * @author 7realm
 */
@ObjectData
public class Channel extends ChatIdEntity {
    /** Serial version UID. */
    private static final long serialVersionUID = 7851489666306197186L;

    /** The channel name. */
    private String name;

    /** The channel type. */
    private ChannelType type;

    /** The channel create date. */
    private Date createDate;

    /** The list of users, present in channel. */
    private List<User> users = new ArrayList<User>();

    /** The list of messages, posted at channel. */
    private List<ChatMessage> messages = new ArrayList<ChatMessage>();

    public Channel() {
        // empty default constructor
    }

    /**
     * @param channelId
     * @param name
     * @param createDate
     */
    public Channel(long channelId, String name, ChannelType type, Date createDate) {
        this.name = name;
        this.type = type;
        this.createDate = createDate;

        setId(channelId);
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

    /**
     * @return
     */
    public ChannelType getType() {
        return type;
    }

    public static enum ChannelType {
        PUBLIC, PRIVATE, PASSWORD
    }
}
