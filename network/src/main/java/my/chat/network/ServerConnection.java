package my.chat.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import my.chat.exceptions.ChatException;
import my.chat.exceptions.ChatIOException;

import static my.chat.commons.ArgumentHelper.checkPositive;
import static my.chat.commons.ArgumentHelper.checkState;

public class ServerConnection implements Runnable {
	private final Thread thread = new Thread(this);
	private final ServerSocket serverSocket;
	private ConnectionState state;

	private OnConnectionListener connectionListener;
	private OnServerCloseListener closeListener;
	private ExceptionHandler handler;

	public ServerConnection(int port) throws ChatIOException {
		checkPositive("port", port);

		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new ChatIOException("Failed to start listen at port '" + port + "'.", e);
		}

		state = ConnectionState.CREATED;
	}

	/**
	 * Start listening to new connections.
	 */
	public void start() {
		checkState(state, ConnectionState.CREATED);

		// start inner thread
		thread.start();

		state = ConnectionState.STARTED;
	}

	/**
	 * Stop listening to new connections, allows multiple call.
	 */
	public void stop() {
		checkState(state, ConnectionState.STARTED, ConnectionState.STOP_REQUESED);

		state = ConnectionState.STOP_REQUESED;
	}

	@Override
	public void run() {
		checkState(state, ConnectionState.STARTED);

		Exception occurredException = null;
		while (state != ConnectionState.STOP_REQUESED) {
			try {
				// TODO interrupt
				Socket socket = serverSocket.accept();
				ClientConnection connection = new ClientConnection(socket);

				if (connectionListener != null) {
					connectionListener.onConnection(connection);
				}
			} catch (IOException e) {
				// TODO only log
				e.printStackTrace();
				occurredException = e;
			} catch (ChatException e) {
				// TODO only log
				e.printStackTrace();
				occurredException = e;
			}

			// try to handle exception
			if (handler != null && handler.canHandle(occurredException)) {
				// exception is handled
				occurredException = null;
			} else {
				break;
			}
		}

		state = ConnectionState.STOPPED;

		// close
		try {
			serverSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// call close listener
		if (closeListener != null) {
			closeListener.onClose(this, occurredException);
		}

		state = ConnectionState.CLOSED;
	}

	public void setOnConnectionListener(OnConnectionListener connectionListener) {
		this.connectionListener = connectionListener;
	}

	public void setOnCloseListener(OnServerCloseListener closeListener) {
		this.closeListener = closeListener;
	}

	public void setExceptionHandler(ExceptionHandler handler) {
		this.handler = handler;
	}
	
	@Override
	public String toString() {
		return "Server connection. State: " + state + ".";
	}
}
