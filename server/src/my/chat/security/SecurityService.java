/**
 * 
 */
package my.chat.security;

import static my.chat.commons.ArgumentHelper.checkNotNull;
import my.chat.db.DatabaseServiceRemote;
import my.chat.db.LoginChatException;
import my.chat.exceptions.ChatException;
import my.chat.exceptions.ChatRuntimeException;
import my.chat.logging.Log;
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
 * This class performs security check for incoming connections. It checks user credentials and on success delegates connection to
 * {@link CommandProcessor#acceptConnection(ClientConnection, User)}.
 * <p>
 * <b>Thread safe:</b> Yes, because it only uses remote interface to database service which is supposed to be thread safe.
 * 
 * @author 7realm
 */
public final class SecurityService implements OnCommandListener, OnConnectionListener, OnClientCloseListener, ExceptionHandler {
    private final DatabaseServiceRemote databaseService;

    public SecurityService(DatabaseServiceRemote databaseService) throws ChatException {
        this.databaseService = databaseService;
    }

    @Override
    public void onConnection(ClientConnection connection) throws ChatException {
        // assign connection to self
        connection.setExceptionHandler(this);
        connection.setOnCloseListener(this);
        connection.setOnCommandlistener(this);

        Log.info(this, "%1 is received for security check.", connection);
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

                Log.info(this, "%1 is accepted for user %2.", connection, user.getUsername());

                // server should reassign connection to him
                CommandProcessor.getInstance().acceptConnection(connection, user);
            } catch (ChatRuntimeException e) {
                Log.info(this, e, "Connection login attempt is rejected.");

                // stop connection
                connection.stop();
            } catch (LoginChatException e) {
                Log.info(this, e, "Connection login attempt is rejected.");

                // stop connection
                connection.stop();
            }
            break;

        default:
            Log.info(this, "Ignoring not login command %1.", command);
            break;
        }
    }

    @Override
    public boolean canHandle(Exception e) {
        return false;
    }

    @Override
    public void onClose(ClientConnection connection, Exception occurredException) {
        // empty
    }
}
