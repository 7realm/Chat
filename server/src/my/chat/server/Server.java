package my.chat.server;

import my.chat.commands.ChatCommand;
import my.chat.exceptions.ChatIOException;
import my.chat.network.ClientConnection;
import my.chat.network.Command;
import my.chat.network.NetworkService;
import my.chat.network.OnCommandListener;
import my.chat.network.OnConnectionListener;

public class Server implements OnCommandListener, OnConnectionListener {
	private final NetworkService networkService;

	public Server() throws ChatIOException {
		networkService = new NetworkService();
		networkService.setOnCommandListener(this);
		networkService.setOnConnectionListener(this);
	}

	public void start() {
		networkService.start();
	}

	@Override
	public void onCommand(ClientConnection connection, Command command) throws ChatIOException {
		System.out.println("Received command: " + command);
		
		if (command instanceof ChatCommand) {
			ChatCommand chatCommand = (ChatCommand) command;

			networkService.sendCommand(connection, new ChatCommand("server", "Hello '" + chatCommand.getUsername() + "'."));
			networkService.sendCommand(connection, new ChatCommand("server", "reply to: " + chatCommand.getMessage()));
		}
	}

	@Override
	public void onConnection(ClientConnection connection) throws ChatIOException {
		System.out.println("Received connection: " + connection);
		
		networkService.sendCommand(connection, new ChatCommand("server", "Welcome to Chat!"));
	}

	public static void main(String[] args) throws ChatIOException {
		Server server = new Server();

		server.start();
		System.out.println("Server started.");
	}
}
