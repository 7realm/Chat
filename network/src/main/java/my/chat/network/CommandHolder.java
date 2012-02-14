package my.chat.network;

import java.io.Serializable;

public class CommandHolder implements Serializable {
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
