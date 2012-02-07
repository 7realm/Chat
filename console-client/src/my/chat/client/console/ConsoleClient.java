package my.chat.client.console;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import my.chat.commands.ChatCommand;
import my.chat.exceptions.ChatIOException;
import my.chat.network.ClientConnection;
import my.chat.network.Command;
import my.chat.network.OnCommandListener;

public class ConsoleClient {
	private static ClientConnection clientConnection;

	public static void main(String[] args) throws ChatIOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

		while (true) {
			try {
				String line = reader.readLine();
				if (line == null || line.length() == 0) {
					break;
				}

				if (line.equalsIgnoreCase("connect")) {
					clientConnection = new ClientConnection("localhost", 8844);
					clientConnection.setOnCommandlistener(new OnCommandListener() {
						@Override
						public void onCommand(ClientConnection connection, Command command) throws ChatIOException {
							if (command instanceof ChatCommand) {
								ChatCommand chatCommand = (ChatCommand) command;
								System.out.println(">>> " + chatCommand.getUsername() + ": " + chatCommand.getMessage());
							}

						}
					});
					clientConnection.start();
				} else if (line.startsWith("send")) {
					String[] parts = line.split(" ");

					Command chatCommand = new ChatCommand("me", parts[1]);
					clientConnection.sendCommand(chatCommand);
				} else if (line.equalsIgnoreCase("close")) {
					clientConnection.stop();
				} else {
					System.out.println("Incorrect command.");
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		System.out.println("Shutdown.");
	}

}
