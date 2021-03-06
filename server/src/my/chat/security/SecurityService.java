/**
 * 
 */
package my.chat.security;

import static my.chat.commons.ArgumentHelper.checkNotNull;
import my.chat.commands.CommandType;
import my.chat.db.DatabaseService;
import my.chat.db.SecurityChatException;
import my.chat.exceptions.ChatException;
import my.chat.exceptions.ChatIOException;
import my.chat.exceptions.ExceptionHandler;
import my.chat.logging.Log;
import my.chat.model.user.User;
import my.chat.network.NetworkService;
import my.chat.network.command.Command;
import my.chat.network.command.CommandContentException;
import my.chat.network.connection.client.ClientConnection;
import my.chat.network.connection.client.OnClientCloseListener;
import my.chat.network.connection.client.OnCommandListener;
import my.chat.network.connection.server.OnConnectionListener;
import my.chat.parser.ParserChatException;
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
    private final DatabaseService databaseService;
    private final ParserService parserService;
    private final CommandProcessor commandProcessor;
    private final NetworkService networkService;

    public SecurityService(DatabaseService databaseService, ParserService parserService, CommandProcessor commandProcessor, NetworkService networkService) throws ChatException {
        this.databaseService = databaseService;
        this.parserService = parserService;
        this.commandProcessor = commandProcessor;
        this.networkService = networkService;
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
    public void onCommand(ClientConnection connection, byte[] bytes) {
        checkNotNull("connection", connection);
        checkNotNull("byte", bytes);

        try {
            Command command = parserService.unmarshall(bytes);
            try {
                switch (command.getType()) {
                case CREATE:
                    String username = command.getString("username");
                    String password = command.getString("password");

                    // create user in database
                    User user = databaseService.createUser(username, password);

                    Log.info(this, "User %1 is created.", username);

                    // notify about create
                    Command success = new Command(CommandType.CREATE)
                        .addItem("user", user);
                    byte[] successBytes = parserService.marshall(success);
                    networkService.sendCommand(connection, successBytes);
                    break;
                case LOGIN:
                    username = command.getString("username");
                    password = command.getString("password");

                    // check user name
                    user = databaseService.login(username, password);

                    Log.info(this, "%1 is accepted for user %2.", connection, user.getNickname());

                    commandProcessor.acceptConnection(connection, user);
                    break;
                default:
                    Log.info(this, "Ignoring not login or create command %1.", command);
                    break;
                }
            } catch (CommandContentException e) {
                Log.info(this, e, "Connection login attempt is rejected.");

                sendFailure(connection, command, e.getMessage());

                // stop connection
                connection.stop();
            } catch (SecurityChatException e) {
                Log.info(this, e, "Connection login attempt is rejected.");

                sendFailure(connection, command, e.getMessage());

                // stop connection
                connection.stop();
            }
        } catch (ChatException e) {
            Log.warn(this, e, "Unexpected failure while processing command.");

            // stop connection
            connection.stop();
        }
    }

    private void sendFailure(ClientConnection connection, Command command, String message) throws ParserChatException,
        ChatIOException {
        Command failure = new Command(CommandType.FAILURE)
            .addItem("message", message)
            .addItem("commandId", command.getId())
            .addItem("commandType", command.getType());
        byte[] failBytes = parserService.marshall(failure);
        networkService.sendCommand(connection, failBytes);
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
