package my.chat.client.console;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import my.chat.exceptions.ChatIOException;
import my.chat.messages.ChatMessage;
import my.chat.network.ClientConnection;
import my.chat.network.Message;
import my.chat.network.OnMessageListener;

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
					clientConnection.setOnMessagelistener(new OnMessageListener() {
						@Override
						public void onMessage(ClientConnection connection, Message message) throws ChatIOException {
							if (message instanceof ChatMessage) {
								ChatMessage chatMessage = (ChatMessage) message;
								System.out.println(">>> " + chatMessage.getUsername() + ": " + chatMessage.getMessage());
							}

						}
					});
					clientConnection.start();
				} else if (line.startsWith("send")) {
					String[] parts = line.split(" ");

					Message chatMessage = new ChatMessage("me", parts[1]);
					clientConnection.sendMessage(chatMessage);
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
