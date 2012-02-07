package my.chat.server;

import my.chat.exceptions.ChatIOException;
import my.chat.messages.ChatMessage;
import my.chat.network.ClientConnection;
import my.chat.network.Message;
import my.chat.network.NetworkService;
import my.chat.network.OnConnectionListener;
import my.chat.network.OnMessageListener;

public class Server implements OnMessageListener, OnConnectionListener {
	private final NetworkService networkService;

	public Server() throws ChatIOException {
		networkService = new NetworkService();
		networkService.setOnMessageListener(this);
		networkService.setOnConnectionListener(this);
	}

	public void start() {
		networkService.start();
	}

	@Override
	public void onMessage(ClientConnection connection, Message message) throws ChatIOException {
		System.out.println("Received message: " + message);
		
		if (message instanceof ChatMessage) {
			ChatMessage chatMessage = (ChatMessage) message;

			networkService.sendMessage(connection, new ChatMessage("server", "Hello '" + chatMessage.getUsername() + "'."));
			networkService.sendMessage(connection, new ChatMessage("server", "reply to: " + chatMessage.getMessage()));
		}
	}

	@Override
	public void onConnection(ClientConnection connection) throws ChatIOException {
		System.out.println("Received connection: " + connection);
		
		networkService.sendMessage(connection, new ChatMessage("server", "Welcome to Chat!"));
	}

	public static void main(String[] args) throws ChatIOException {
		Server server = new Server();

		server.start();
		System.out.println("Server started.");
	}
}
