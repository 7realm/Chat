/**
 * 
 */
package my.chat.security;

import static my.chat.commons.ArgumentHelper.checkNotNull;
import my.chat.db.DatabaseServiceRemote;
import my.chat.db.LoginChatException;
import my.chat.exceptions.ChatException;
import my.chat.exceptions.ChatRuntimeException;
import my.chat.model.User;
import my.chat.network.ClientConnection;
import my.chat.network.Command;
import my.chat.network.ExceptionHandler;
import my.chat.network.OnClientCloseListener;
import my.chat.network.OnCommandListener;
import my.chat.network.OnConnectionListener;
import my.chat.parser.ParserService;
import my.chat.server.CommandProcessor;

/**
 * @author taras.kovalchuk
 * 
 */
public final class SecurityService implements OnCommandListener, OnConnectionListener, OnClientCloseListener, ExceptionHandler {
    private final DatabaseServiceRemote databaseService;

    public SecurityService(DatabaseServiceRemote databaseService) throws ChatException {
        // TODO split to separate services
        this.databaseService = databaseService;
    }

    @Override
    public void onConnection(ClientConnection connection) throws ChatException {
        // assign connection to self
        connection.setExceptionHandler(this);
        connection.setOnCloseListener(this);
        connection.setOnCommandlistener(this);
    }

    @Override
    public void onCommand(ClientConnection connection, byte[] bytes) throws ChatException {
        checkNotNull("connection", connection);
        checkNotNull("byte", bytes);

        Command command = ParserService.getInstance().unmarshall(bytes);

        switch (command.getType()) {
        case LOGIN:
            try {
                // check user name
                User user = databaseService.login(command.getString("username"), command.getString("password"));

                // server should reassign connection to him
                CommandProcessor.getInstance().acceptConnection(connection, user);
            } catch (ChatRuntimeException e) {
                // TODO change this
                e.printStackTrace();
                System.out.println("Connection login attempt rejected.");
                // stop connection
                connection.stop();
            } catch (LoginChatException e) {
                e.printStackTrace();
                System.out.println("Connection login attempt rejected.");
                // stop connection
                connection.stop();
            }
            break;

        default:
            // for now ignore other commands
            System.out.println("Ignoring: " + command);
            break;
        }
    }

    @Override
    public boolean canHandle(Exception e) {
        return false;
    }

    @Override
    public void onClose(ClientConnection connection, Exception occurredException) {
        // TODO Auto-generated method stub

    }
}
