package my.chat.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import my.chat.model.commons.ChatEntity;
import my.chat.model.commons.ChatIdEntity;
import my.chat.model.commons.UpdateChatException;
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
    
    public ChannelType getType() {
        return type;
    }
    
    public void setType(ChannelType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public Date getCreateDate() {
        return createDate;
    }
    
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public List<User> getUsers() {
        return users;
    }
    
    public List<ChatMessage> getMessages() {
        return messages;
    }
    
    @Override
    public void update(ChatEntity newEntity) throws UpdateChatException {
        super.update(newEntity);

        Channel newChannel = (Channel) newEntity;
        
        setType(newChannel.getType());
        setName(newChannel.getName());
        setCreateDate(newChannel.getCreateDate());
        
        users = newChannel.getUsers();
        messages = newChannel.getMessages();
    }

    /**
     * Channel type. Private channels can be password protected.
     * <p>
     * <b>Thread safe:</b> No.
     *
     * @author 7realm
     */
    public static enum ChannelType {
        PUBLIC, PRIVATE, PASSWORD
    }
}
