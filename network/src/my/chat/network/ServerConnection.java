package my.chat.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import my.chat.commons.CustomThread;
import my.chat.exceptions.ChatException;
import my.chat.exceptions.ChatIOException;

public class ServerConnection extends CustomThread {
	private final ServerSocket serverSocket;
	private ConnectionListener listener;

	public ServerConnection(int port) throws ChatIOException {
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new ChatIOException("Failed to open connection at port '" + port + "'.", e);
		}
	}

	@Override
	protected void execute() {
		try {
			Socket socket = serverSocket.accept();
			ClientConnection connection = new ClientConnection(socket);
			if (listener != null) {
				listener.onConnection(connection);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ChatIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setOnConnectionListener(ConnectionListener listener) {
		this.listener = listener;
	}

	public void close() {
		try {
			serverSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static interface ConnectionListener {
		void onConnection(ClientConnection connection) throws ChatIOException;
	}

	@Override
	protected void handleException(ChatException e) {
		close();
	}
}
