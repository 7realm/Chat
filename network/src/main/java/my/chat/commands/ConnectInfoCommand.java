package my.chat.commands;

import java.util.List;

import my.chat.model.Channel;
import my.chat.model.PrivateMessage;
import my.chat.model.User;
import my.chat.network.Command;

public class ConnectInfoCommand extends Command {
    private static final long serialVersionUID = -4481765530117326206L;
    
    private User user;
    
    private List<PrivateMessage> offlineMessages;
    
    private List<Channel> publicChannels;

    /**
     * @param user
     * @param offlineMessages
     * @param publicChannels
     */
    public ConnectInfoCommand(User user, List<PrivateMessage> offlineMessages, List<Channel> publicChannels) {
        super();
        this.user = user;
        this.offlineMessages = offlineMessages;
        this.publicChannels = publicChannels;
    }

    /**
     * @return the user
     */
    public User getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * @return the offlineMessages
     */
    public List<PrivateMessage> getOfflineMessages() {
        return offlineMessages;
    }

    /**
     * @param offlineMessages the offlineMessages to set
     */
    public void setOfflineMessages(List<PrivateMessage> offlineMessages) {
        this.offlineMessages = offlineMessages;
    }

    /**
     * @return the publicChannels
     */
    public List<Channel> getPublicChannels() {
        return publicChannels;
    }

    /**
     * @param publicChannels the publicChannels to set
     */
    public void setPublicChannels(List<Channel> publicChannels) {
        this.publicChannels = publicChannels;
    }
}
