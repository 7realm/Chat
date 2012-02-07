package my.chat.network;

import my.chat.exceptions.ChatException;

public interface OnMessageListener {
	/**
	 * This handler is called when message is received via network.
	 * 
	 * @param connection
	 * @param message
	 * @throws ChatException
	 */
	void onMessage(ClientConnection connection, Message message) throws ChatException;
}