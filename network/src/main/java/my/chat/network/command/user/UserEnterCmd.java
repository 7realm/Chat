package my.chat.network.command.user;

import my.chat.commands.CommandType;
import my.chat.model.UpdateChatException;
import my.chat.model.user.User;
import my.chat.network.command.BaseCommand;
import my.chat.network.command.CommandContentException;

/**
 * User enter command.
 * <p>
 * <b>Thread safe:</b> No.
 * 
 * @author 7realm
 */
public class UserEnterCmd extends BaseCommand {
    /** The user that entered. */
    private User user;

    @Override
    public CommandType getType() {
        return CommandType.USER_ENTER;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) throws CommandContentException {
        checkIsSet("user", user);
        
        try {
            this.user = user.createTransferObject();
        } catch (UpdateChatException e) {
            throw new CommandContentException("Failed to create user %1 transer object.", e, user.getId());
        }
    }
}
