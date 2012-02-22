package my.chat.network.connection.server;

import my.chat.exceptions.ChatException;
import my.chat.network.connection.client.ClientConnection;

public interface OnConnectionListener {
	void onConnection(ClientConnection connection) throws ChatException;
}