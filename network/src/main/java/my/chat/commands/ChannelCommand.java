/**
 * 
 */
package my.chat.commands;

import my.chat.model.Channel;
import my.chat.network.Command;

/**
 * 
 * <p>
 * <b>Thread safe:</b> No.
 *
 * @author 7realm
 */
public abstract class ChannelCommand extends Command {
    private static final long serialVersionUID = -9073451574271142733L;
    
    private Channel channel;
    
    /**
     * @param channel
     */
    protected ChannelCommand(Channel channel) {
        this.channel = channel;
    }

    public Channel getChannel() {
        return channel;
    }
    
    public void setChannel(Channel channel) {
        this.channel = channel;
    }
}
