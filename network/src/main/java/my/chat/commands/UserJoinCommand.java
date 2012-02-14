package my.chat.commands;

import my.chat.model.Channel;
import my.chat.model.User;

public class UserJoinCommand extends ChannelCommand {
    private static final long serialVersionUID = 5749312504458260489L;
    
    private User user;

    /**
     * @param channel
     * @param user
     */
    public UserJoinCommand(Channel channel, User user) {
        super(channel);
        this.user = user;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
}