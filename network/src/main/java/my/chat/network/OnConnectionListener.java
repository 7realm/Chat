package my.chat.network;

import my.chat.exceptions.ChatException;

public interface OnConnectionListener {
	void onConnection(ClientConnection connection) throws ChatException;
}