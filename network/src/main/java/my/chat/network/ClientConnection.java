package my.chat.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import my.chat.exceptions.ChatException;
import my.chat.exceptions.ChatIOException;

import static my.chat.commons.ArgumentHelper.checkNotNull;
import static my.chat.commons.ArgumentHelper.checkPositive;
import static my.chat.commons.ArgumentHelper.checkState;

/**
 * Connection class that wraps socket functions. Has an internal thread that will read incoming message.
 * <p>
 * It can be created from existing socket, or can be connected to remote host.
 * <p>
 * After creation all handlers and listeners should be set, and only after this connection should be started.
 * <p>
 * After start, connection can be stopped, by requesting stop command. After stop is done, connection is closed automatically.
 * 
 * @author taras.kovalchuk
 */
public class ClientConnection implements Runnable {
	private final Thread thread = new Thread(this);
	private final Socket socket;
	private ConnectionState state = ConnectionState.CREATED;
	private Exception occurredException;

	private OnMessageListener messagelistener;
	private OnClientCloseListener closeListener;
	private ExceptionHandler handler;

	/**
	 * Create connection form incoming socket.
	 * 
	 * @param socket the incoming socket
	 */
	public ClientConnection(Socket socket) {
		checkNotNull("socket", socket);
		this.socket = socket;
	}

	/**
	 * Create connection using remote host and port.
	 * 
	 * @param host the remote host
	 * @param port the remote port
	 * @throws ChatIOException if exception occurred while connecting to remote host
	 */
	public ClientConnection(String host, int port) throws ChatIOException {
		checkNotNull("host", host);
		checkPositive("port", port);

		try {
			socket = new Socket(host, port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new ChatIOException("Failed to open connection to '" + host + ":" + port + "'.", e);
		}
	}

	/**
	 * Start listening to messages.
	 */
	public void start() {
		checkState(state, ConnectionState.CREATED);

		// start inner thread
		thread.start();

		state = ConnectionState.STARTED;
	}

	/**
	 * Stop listening to messages, allows multiple call.
	 */
	public void stop() {
		checkState(state, ConnectionState.STARTED, ConnectionState.STOP_REQUESED);

		state = ConnectionState.STOP_REQUESED;
	}

	@Override
	public void run() {
		checkState(state, ConnectionState.STARTED);

		try {
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

			while (state != ConnectionState.STOP_REQUESED) {
				try {
					// TODO interrupt
					Message message = (Message) in.readObject();

					if (messagelistener != null) {
						messagelistener.onMessage(this, message);
					}
				} catch (IOException e) {
					// TODO only log
					e.printStackTrace();
					occurredException = e;
				} catch (ClassNotFoundException e) {
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
		} catch (IOException e) {
			// exception occurred while opening input stream
			e.printStackTrace();

			// try to handle just in case, but this is not recoverable error
			occurredException = e;
			if (handler == null) {
				handler.canHandle(e);
			}
		}

		state = ConnectionState.STOPPED;

		// close
		close();
	}

	/**
	 * Send message over network. If message sending failed, connection will be closed.
	 * 
	 * @param message the message to send
	 * @throws ChatIOException if I/O error occurred while sending
	 */
	public void sendMessage(Message message) throws ChatIOException {
		checkState(state, ConnectionState.STARTED);

		try {
			ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
			outputStream.writeObject(message);
			outputStream.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			// stop connection if failed to handle error
			if (handler == null || !handler.canHandle(e)) {
				occurredException = e;
				stop();
			}

			// anyway we throw exception to show that send failed
			throw new ChatIOException("Failed to send Message.", e);
		}
	}

	public void setOnMessagelistener(OnMessageListener messagelistener) {
		this.messagelistener = messagelistener;
	}

	public void setOnCloseListener(OnClientCloseListener closeListener) {
		this.closeListener = closeListener;
	}

	public void setExceptionHandler(ExceptionHandler handler) {
		this.handler = handler;
	}

	/**
	 * Do closing work, can be called only from inner thread.
	 * 
	 * @param occurredException the occurred exception
	 */
	protected void close() {
		checkState(state, ConnectionState.STOPPED);

		try {
			socket.close();
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

	@Override
	public String toString() {
		return "Client connection to '" + socket.getInetAddress() + ":" + socket.getPort() + "'. State: " + state + ".";
	}
}
