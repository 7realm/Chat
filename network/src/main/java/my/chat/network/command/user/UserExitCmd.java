/**
 * 
 */
package my.chat.network.command.user;

import my.chat.commands.CommandType;
import my.chat.model.user.User;
import my.chat.network.command.BaseCommand;
import my.chat.network.command.CommandContentException;

/**
 * User exit command.
 * <p>
 * <b>Thread safe:</b> No.
 *
 * @author 7realm
 */
public class UserExitCmd extends BaseCommand {
    /** The user ID. */
    private long userId;
    
    @Override
    public CommandType getType() {
       return CommandType.USER_EXIT;
    }
    
    public long getUserId() {
        return userId;
    }
    
    public void setUser(User user) throws CommandContentException {
        checkIsSet("user", user);
        userId = user.getId();
    }
}
