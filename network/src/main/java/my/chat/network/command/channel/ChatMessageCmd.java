/**
 * 
 */
package my.chat.network.command.channel;

import my.chat.commands.CommandType;
import my.chat.model.UpdateChatException;
import my.chat.model.messages.ChatMessage;
import my.chat.network.command.BaseCommand;
import my.chat.network.command.CommandContentException;

/**
 * Command for chat message.
 * <p>
 * <b>Thread safe:</b> No.
 * 
 * @author 7realm
 */
public class ChatMessageCmd extends BaseCommand {
    private ChatMessage message;

    private long authorId;

    private long channelId;

    private long replyToId;

    @Override
    public CommandType getType() {
        return CommandType.CHANNEL_MESSAGE;
    }

    public ChatMessage getMessage() {
        return message;
    }
    
    public long getAuthorId() {
        return authorId;
    }
    
    public long getChannelId() {
        return channelId;
    }
    
    public long getReplyToId() {
        return replyToId;
    }

    public void setMessage(ChatMessage message) throws CommandContentException {
        checkIsSet("message", message);
        checkIsSet("message.author", message.getAuthor());
        checkIsSet("message.channel", message.getChannel());
        checkIsSet("message.authorDate", message.getAuthorDate());

        try {
            this.message = message.createTransferObject();
            authorId = message.getAuthor().getId();
            channelId = message.getChannel().getId();
            replyToId = message.getReplyTo() == null ? 0 : message.getReplyTo().getId();
        } catch (UpdateChatException e) {
            throw new CommandContentException("Failed to set message with id %1, for user %2 to channel %3.", e, message.getId(),
                message.getAuthor().getId(), message.getChannel().getId());
        }
    }

}
