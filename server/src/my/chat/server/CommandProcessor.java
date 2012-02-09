package my.chat.server;

import my.chat.exceptions.ChatException;
import my.chat.model.User;
import my.chat.network.ClientConnection;
import my.chat.network.Command;
import my.chat.network.OnClientCloseListener;
import my.chat.network.OnCommandListener;
import my.chat.network.OnConnectionListener;

public final class CommandProcessor implements OnConnectionListener, OnCommandListener, OnClientCloseListener {
    private final static CommandProcessor INSTANCE = new CommandProcessor();

    private CommandProcessor() {
        // empty
    }

    public CommandProcessor getInstance() {
        return INSTANCE;
    }

    public void acceptConnection(ClientConnection connection, User user) {

    }

    @Override
    public void onClose(ClientConnection connection, Exception occurredException) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onCommand(ClientConnection connection, Command command) throws ChatException {
        // TODO Auto-generated method stub

    }

    @Override
    public void onConnection(ClientConnection connection) throws ChatException {
        // TODO Auto-generated method stub
    }

}
