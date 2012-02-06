package my.chat.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import my.chat.commons.CustomThread;
import my.chat.exceptions.ChatException;
import my.chat.exceptions.ChatIOException;

public class ClientConnection extends CustomThread {
	private final Socket socket;
	private MessageListener listener;

	public ClientConnection(Socket socket) {
		this.socket = socket;
	}

	public ClientConnection(String host, int port) throws ChatIOException {
		try {
			socket = new Socket(host, port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new ChatIOException("Failed to open connection to '" + host + ":" + port + "'.", e);
		}
	}

	@Override
	protected void execute() throws ChatIOException {
		try {
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			Message networkPackage = (Message) in.readObject();

			if (listener != null) {
				listener.onMessage(this, networkPackage);
			}
		} catch (IOException e) {
			throw new ChatIOException("Failed to read Message from socket.", e);
		} catch (ClassNotFoundException e) {
			throw new ChatIOException("Failed to deserialize class from package.", e);
		}
	}

	public void sendMessage(Message message) throws ChatIOException {
		try {
			ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
			outputStream.writeObject(message);
			outputStream.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new ChatIOException("Failed to send Message.", e);
		}
	}

	public void setOnMessageListener(MessageListener listener) {
		this.listener = listener;
	}

	public void close() {
		try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static interface MessageListener {
		void onMessage(ClientConnection connection, Message message) throws ChatIOException;
	}

	@Override
	protected void handleException(ChatException e) {
		close();
	}

}
