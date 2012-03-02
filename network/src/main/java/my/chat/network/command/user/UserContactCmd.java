/**
 * 
 */
package my.chat.network.command.user;

import my.chat.commands.CommandType;
import my.chat.model.user.User;
import my.chat.network.command.BaseCommand;
import my.chat.network.command.CommandContentException;
import my.chat.network.command.Ignore;

import static my.chat.commons.ArgumentHelper.checkState;

/**
 * This request occurs when user adds, removes, edits contact from its contact list.
 * <p>
 * <b>Thread safe:</b> No.
 * 
 * @author 7realm
 */
public class UserContactCmd extends BaseCommand {
    /** The id of user, which contacts should be changed. */
    private long hostUserId;

    /** The id of user, that will be added, edited in contacts. */
    private long affectedUserId;

    /** The given name for user, is set by default to added user nickname. */
    private String givenName;

    /** The exact command type, can be {@link CommandType#USER_ADD_CONTACT}, {@link CommandType#USER_REMOVE_CONTACT}. */
    @Ignore
    private CommandType type;
    
    /**
     * Gets command type, can be {@link CommandType#USER_ADD_CONTACT}, {@link CommandType#USER_REMOVE_CONTACT}.
     * 
     * @return the command type
     * 
     * @throws IllegalStateException if command type value is not set
     */
    @Override
    public CommandType getType() {
        checkState("type", type);
        return type;
    }

    public long getHostUserId() {
        return hostUserId;
    }

    public long getAffectedUserId() {
        return affectedUserId;
    }

    public void setHostUser(User hostUser) throws CommandContentException {
        checkIsSet("hostUser", hostUser);
        hostUserId = hostUser.getId();
    }

    public void setAffectedUser(User affectedUser) throws CommandContentException {
        checkIsSet("affectedUser", affectedUser);

        affectedUserId = affectedUser.getId();

        // additionally set given name if it is not set
        if (givenName == null) {
            givenName = affectedUser.getNickname();
        }
    }

    public void setGivenName(String givenName) throws CommandContentException {
        checkIsEmpty("givenName", givenName);

        this.givenName = givenName;
    }

    public void setType(CommandType type) throws CommandContentException {
        checkIsSet("type", type);

        if (CommandType.USER_ADD_CONTACT.equals(type) || CommandType.USER_REMOVE_CONTACT.equals(type)) {
            this.type = type;
        } else {
            throw new CommandContentException("Unsupported command type %1 for user contact command.", type);
        }
    }
}
