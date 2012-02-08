package my.chat.server;

import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import my.chat.commands.ChatCommand;
import my.chat.db.DatabaseServiceRemote;
import my.chat.exceptions.ChatException;
import my.chat.exceptions.ChatIOException;
import my.chat.model.User;
import my.chat.network.ClientConnection;
import my.chat.network.Command;
import my.chat.network.ExceptionHandler;
import my.chat.network.NetworkService;
import my.chat.network.OnClientCloseListener;
import my.chat.network.OnCommandListener;
import my.chat.security.SecurityService;

public class Server implements OnCommandListener, OnClientCloseListener, ExceptionHandler {
	private final NetworkService networkService;
	private final SecurityService securityService;
	private final DatabaseServiceRemote databaseService;

	private final Map<User, ClientConnection> users = Collections.synchronizedMap(new HashMap<User, ClientConnection>());
	
	public Server() throws ChatException {
		try {
			final Hashtable<String, String> jndiProperties = new Hashtable<String, String>();
			jndiProperties.put(Context.INITIAL_CONTEXT_FACTORY, "org.jnp.interfaces.NamingContextFactory"); 
			jndiProperties.put(Context.PROVIDER_URL, "localhost:1099"); 
	        jndiProperties.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
	        final Context context = new InitialContext(jndiProperties);
	        // The app name is the application name of the deployed EJBs. This is typically the ear name
	        // without the .ear suffix. However, the application name could be overridden in the application.xml of the
	        // EJB deployment on the server.
	        final String appName = "chat-ear";
	        // This is the module name of the deployed EJBs on the server. This is typically the jar name of the
	        // EJB deployment, without the .jar suffix, but can be overridden via the ejb-jar.xml
	        final String moduleName = "db";
	        // AS7 allows each deployment to have an (optional) distinct name. We haven't specified a distinct name for
	        // our EJB deployment, so this is an empty string
	        final String distinctName = "";
	        // The EJB name which by default is the simple class name of the bean implementation class
	        final String beanName = "db_ejb";
	        // the remote view fully qualified class name
	        final String viewClassName = DatabaseServiceRemote.class.getName();
	        // let's do the lookup
//	        databaseService = (DatabaseServiceRemote) context.lookup("ejb:" + appName + "/" + moduleName + "/" + distinctName + "/" + beanName + "!" + viewClassName);
	        databaseService = (DatabaseServiceRemote) context.lookup("db_service");
		} catch (NamingException e) {
			throw new ChatException("Failed to initialize database service.", e);
		}
		
		securityService = new SecurityService(databaseService);
		
		networkService = new NetworkService();
		networkService.setOnCommandListener(securityService);
		networkService.setOnConnectionListener(securityService);
	}

	public void start() {
		networkService.start();
	}

	@Override
	public void onCommand(ClientConnection connection, Command command) throws ChatIOException {
		System.out.println("Received command: " + command);

		if (command instanceof ChatCommand) {
			ChatCommand chatCommand = (ChatCommand) command;
			User user = (User) connection.getTag();

			networkService.sendCommand(connection, new ChatCommand("server", "hello '" + user.getUsername() + "'."));
			networkService.sendCommand(connection, new ChatCommand("server", "reply to: " + chatCommand.getMessage()));
		}
	}

	public void onConnection(ClientConnection connection, User user) throws ChatIOException {
		// associate user with connection
		users.put(user, connection);
		connection.setTag(user);
		
		// reassign connection to server
		connection.setOnCloseListener(this);
		connection.setOnCommandlistener(this);
		connection.setExceptionHandler(this);
		
		networkService.sendCommand(connection, new ChatCommand("server", "Welcome to Chat, " + user.getUsername() + "!"));
	}

	private static Server server;

	public static Server getInstance() {
		return server;
	}

	public static void main(String[] args) throws ChatException {
		server = new Server();

		server.start();
		System.out.println("Server started.");
	}

	@Override
	public void onClose(ClientConnection connection, Exception occurredException) {
		// clear association
		users.remove(connection.getTag());
		connection.setTag(null);
	}

	@Override
	public boolean canHandle(Exception e) {
		// TODO Auto-generated method stub
		return false;
	}
}
