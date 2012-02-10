package my.chat.commands;

import my.chat.model.Channel;
import my.chat.model.User;

public class UserLeaveCommand extends ChannelCommand {
    private static final long serialVersionUID = 4089653332097961982L;
    
    private User user;

    /**
     * @param channel
     * @param user
     */
    public UserLeaveCommand(Channel channel, User user) {
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
