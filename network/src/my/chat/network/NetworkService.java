package my.chat.network;

import my.chat.exceptions.ChatIOException;
import my.chat.network.ClientConnection.MessageListener;
import my.chat.network.ServerConnection.ConnectionListener;

public class NetworkService implements ConnectionListener {
	private final ServerConnection serverConnection;
	private MessageListener messageListener;
	private ConnectionListener connectionListener;

	public NetworkService() throws ChatIOException {
		serverConnection = new ServerConnection(8844);
		serverConnection.setOnConnectionListener(this);
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
	public void onConnection(ClientConnection connection) throws ChatIOException {
		connection.setOnMessageListener(messageListener);
		connection.start();

		if (connectionListener != null) {
			connectionListener.onConnection(connection);
		}
	}

	public void setOnMessageListener(MessageListener listener) {
		this.messageListener = listener;
	}

	public void setOnConnectionListener(ConnectionListener listener) {
		this.connectionListener = listener;
	}
}
