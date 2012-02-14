package my.chat.network;

import my.chat.exceptions.ChatException;

public interface OnCommandListener {
	/**
	 * This handler is called when command is received via network.
	 * 
	 * @param connection the client connection
	 * @param bytes the received command
	 * @throws ChatException if error occurs while processing command
	 */
	void onCommand(ClientConnection connection, byte[] bytes) throws ChatException;
}