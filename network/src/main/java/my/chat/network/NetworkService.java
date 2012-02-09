package my.chat.network;

import my.chat.exceptions.ChatException;
import my.chat.exceptions.ChatIOException;

public class NetworkService implements OnConnectionListener, OnCommandListener, OnClientCloseListener, OnServerCloseListener,
	ExceptionHandler {
	
	private final ServerConnection serverConnection;
	
	private OnCommandListener commandListener;
	private OnConnectionListener connectionListener;
	private OnClientCloseListener clientCloseListener;
	private OnServerCloseListener serverCloseListener;

	public NetworkService() throws ChatIOException {
		serverConnection = new ServerConnection(NetworkConfig.SERVER_PORT);

		serverConnection.setOnConnectionListener(this);
		serverConnection.setOnCloseListener(this);
	}

	public void start() {
		serverConnection.start();
	}

	public void stop() {
		serverConnection.stop();
	}

	public void sendCommand(ClientConnection connection, Command command) throws ChatIOException {
		connection.sendCommand(command);
	}

	@Override
	public void onConnection(ClientConnection connection) throws ChatException {
		// assign connection to self and start it
		connection.setOnCommandlistener(this);
		connection.setOnCloseListener(this);
		connection.setExceptionHandler(this);
		connection.start();

		// notify about connection
		if (connectionListener != null) {
			connectionListener.onConnection(connection);
		}
	}
	
	@Override
	public void onCommand(ClientConnection connection, Command command) throws ChatException {
		if (commandListener != null) {
			commandListener.onCommand(connection, command);
		}

	}

	@Override
	public void onClose(ServerConnection connection, Exception occurredException) {
		if (serverCloseListener != null) {
			serverCloseListener.onClose(connection, occurredException);
		}
	}

	@Override
	public void onClose(ClientConnection connection, Exception occurredException) {
		if (clientCloseListener != null) {
			clientCloseListener.onClose(connection, occurredException);
		}
	}

	@Override
	public boolean canHandle(Exception e) {
		// TODO maybe we can allow to handle some exceptions
		return false;
	}
	
	public void setOnCommandListener(OnCommandListener listener) {
		this.commandListener = listener;
	}

	public void setOnConnectionListener(OnConnectionListener listener) {
		this.connectionListener = listener;
	}
	
	public void setOnClientCloseListener(OnClientCloseListener clientCloseListener) {
		this.clientCloseListener = clientCloseListener;
	}
	
	public void setOnServerCloseListener(OnServerCloseListener serverCloseListener) {
		this.serverCloseListener = serverCloseListener;
	}
}
