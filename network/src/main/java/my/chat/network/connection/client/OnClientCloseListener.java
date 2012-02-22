package my.chat.network.connection.client;

public interface OnClientCloseListener {
	/**
	 * This method is called when connection is finished listening and socket is closed.
	 * 
	 * @param connection the current connection
	 * @param occurredException the exception that occurred before close, can be null
	 */
	void onClose(ClientConnection connection, Exception occurredException);
}