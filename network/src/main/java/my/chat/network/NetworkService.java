package my.chat.network;

import my.chat.exceptions.ChatException;
import my.chat.exceptions.ChatIOException;

public class NetworkService implements OnConnectionListener, OnMessageListener, OnClientCloseListener, OnServerCloseListener,
	ExceptionHandler {
	
	private final ServerConnection serverConnection;
	
	private OnMessageListener messageListener;
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

	public void sendMessage(ClientConnection connection, Message message) throws ChatIOException {
		connection.sendMessage(message);
	}

	@Override
	public void onConnection(ClientConnection connection) throws ChatException {
		connection.setOnMessagelistener(this);
		connection.setOnCloseListener(this);
		connection.start();

		if (connectionListener != null) {
			connectionListener.onConnection(connection);
		}
	}
	
	@Override
	public void onMessage(ClientConnection connection, Message message) throws ChatException {
		if (messageListener != null) {
			messageListener.onMessage(connection, message);
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
	
	public void setOnMessageListener(OnMessageListener listener) {
		this.messageListener = listener;
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
