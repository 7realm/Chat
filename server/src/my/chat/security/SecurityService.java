/**
 * 
 */
package my.chat.security;

import my.chat.commands.LoginCommand;
import my.chat.db.ChatLoginException;
import my.chat.db.DatabaseServiceRemote;
import my.chat.exceptions.ChatException;
import my.chat.model.User;
import my.chat.network.ClientConnection;
import my.chat.network.Command;
import my.chat.network.ExceptionHandler;
import my.chat.network.OnClientCloseListener;
import my.chat.network.OnCommandListener;
import my.chat.network.OnConnectionListener;
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
	public void onCommand(ClientConnection connection, Command command) throws ChatException {
		if (command instanceof LoginCommand) {
			LoginCommand loginCommand = (LoginCommand) command;
			try {
				// check user name
				User user = databaseService.login(loginCommand.getUsername(), loginCommand.getPassword());

				// server should reassign connection to him
				CommandProcessor.getInstance().acceptConnection(connection, user);
			} catch (ChatLoginException e) {
				e.printStackTrace();
				System.out.println("Connection login attempt rejected.");
				// stop connection
				connection.stop();
			}
		} else {
			// for now ignore other commands
			System.out.println("Ignoring: " + command);
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
