package my.chat.network.command.channel;

import my.chat.commands.CommandType;
import my.chat.model.channel.Channel;
import my.chat.model.user.User;
import my.chat.network.command.BaseCommand;
import my.chat.network.command.CommandContentException;
import my.chat.network.command.Ignore;

import static my.chat.commons.ArgumentHelper.checkState;

/**
 * Represents command when user joins or leaves channels.
 * <p>
 * <b>Thread safe:</b> No.
 * 
 * @author 7realm
 */
public class ChannelUserCmd extends BaseCommand {
    /** The user that join/leave channel. */
    private long userId;

    /** The affected channel ID. */
    private long channelId;

    /** The exact command type, can be {@link CommandType#CHANNEL_JOIN}, {@link CommandType#CHANNEL_LEAVE}. */
    @Ignore
    private CommandType type;

    @Override
    public CommandType getType() {
        checkState("type", type);
        return type;
    }
    
    public long getUserId() {
        return userId;
    }
    
    public long getChannelId() {
        return channelId;
    }

    public void setUser(User user) throws CommandContentException {
        checkIsSet("user", user);
        userId = user.getId();
    }

    public void setChannel(Channel channel) throws CommandContentException {
        checkIsSet("channel", channel);
        channelId = channel.getId();
    }

    public void setType(CommandType type) throws CommandContentException {
        checkIsSet("type", type);

        if (CommandType.CHANNEL_JOIN.equals(type) || CommandType.CHANNEL_LEAVE.equals(type)) {
            this.type = type;
        } else {
            throw new CommandContentException("Unsupported command type %1 for user channel command.", type);
        }
    }

}
