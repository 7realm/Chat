/**
 * 
 */
package my.chat.network.command.user;

import my.chat.commands.CommandType;
import my.chat.model.UpdateChatException;
import my.chat.model.messages.PrivateMessage;
import my.chat.network.command.BaseCommand;
import my.chat.network.command.CommandContentException;

/**
 * 
 * <p>
 * <b>Thread safe:</b> No.
 * 
 * @author 7realm
 */
public class UserMessageCmd extends BaseCommand {
    private PrivateMessage message;
    private long authorId;
    private long recipientId;
    private long replyToId;

    @Override
    public CommandType getType() {
        return CommandType.USER_MESSAGE;
    }

    public PrivateMessage getMessage() {
        return message;
    }
    
    public long getAuthorId() {
        return authorId;
    }
    
    public long getRecipientId() {
        return recipientId;
    }
    
    public long getReplyToId() {
        return replyToId;
    }

    public void setMessage(PrivateMessage message) throws CommandContentException {
        checkIsSet("message", message);
        checkIsSet("message.author", message.getAuthor());
        checkIsSet("message.recipient", message.getRecipient());
        checkIsSet("message.authorDate", message.getAuthorDate());

        try {
            this.message = message.createTransferObject();
            authorId = message.getAuthor().getId();
            recipientId = message.getRecipient().getId();
            replyToId = message.getReplyTo() == null ? 0 : message.getReplyTo().getId();
        } catch (UpdateChatException e) {
            throw new CommandContentException("Failed to set message with id %1, for user %2 to user %3.", e, message.getId(),
                message.getAuthor().getId(), message.getRecipient().getId());
        }
    }
}
