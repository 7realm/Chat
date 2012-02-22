package my.chat.network.connection.server;

public interface OnServerCloseListener {
	/**
	 * This method is called when connection is finished listening and socket is closed.
	 * 
	 * @param connection the current server connection
	 * @param occurredException the exception that occurred before close, can be null
	 */
	void onClose(ServerConnection connection, Exception occurredException);
}