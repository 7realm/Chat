/**
 * 
 */
package my.chat.network.command.login;

import my.chat.commands.CommandType;
import my.chat.network.command.BaseCommand;
import my.chat.network.command.CommandContentException;

/**
 * Represents login command wrapper.
 * <p>
 * <b>Thread safe:</b> No.
 * 
 * @author 7realm
 */
public class LoginCmd extends BaseCommand {
    private String username;

    private String password;

    @Override
    public CommandType getType() {
        return CommandType.LOGIN;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setUsername(String username) throws CommandContentException {
        checkIsEmpty("username", username);
        this.username = username;
    }

    public void setPassword(String password) throws CommandContentException {
        checkIsSet("password", password);
        this.password = password;
    }
}
