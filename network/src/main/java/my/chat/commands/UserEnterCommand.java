package my.chat.commands;

import my.chat.model.User;
import my.chat.network.Command;

public class UserEnterCommand extends Command {
    private static final long serialVersionUID = 4065093833195677093L;

    private User user;

    /**
     * @param user
     */
    public UserEnterCommand(User user) {
        super();
        this.user = user;
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
}
