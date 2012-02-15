package my.chat.network;

import java.io.Serializable;

public class CommandHolder implements Serializable {
    private static final long serialVersionUID = -286433941356351633L;
    
    private byte[] command;
    
    public CommandHolder () {
        
    }
    
    public byte[] getCommand() {
        return command;
    }
    
    public void setCommand(byte[] command) {
        this.command = command;
    }
    
}
