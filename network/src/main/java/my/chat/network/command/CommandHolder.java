package my.chat.network.command;

import java.io.Serializable;

/**
 * This is command holder for command. Currently it is used only fore serialization purposes, but in future this can be some unified command
 * container for different parsers.
 * <p>
 * <b>Thread safe:</b> No.
 * 
 * @author 7realm
 */
public class CommandHolder implements Serializable {
    private static final long serialVersionUID = -286433941356351633L;

    private byte[] command;

    public CommandHolder() {

    }

    public byte[] getCommand() {
        return command;
    }

    public void setCommand(byte[] command) {
        this.command = command;
    }
}
