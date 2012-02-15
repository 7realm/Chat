package my.chat.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import my.chat.exceptions.ChatException;
import my.chat.exceptions.ChatIOException;
import my.chat.logging.Log;

import static my.chat.commons.ArgumentHelper.checkNotNull;
import static my.chat.commons.ArgumentHelper.checkPositive;
import static my.chat.commons.ArgumentHelper.checkState;

import static my.chat.commons.Helper.makeMessage;

/**
 * Connection class that wraps socket functions. Has an internal thread that will read incoming command.
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
    private Object tag;
    private ConnectionState state = ConnectionState.CREATED;
    private Exception occurredException;

    private OnCommandListener commandlistener;
    private OnClientCloseListener closeListener;
    private ExceptionHandler handler;

    /** Output socket stream. It is created in {@link ClientConnection#start()} method. */
    private ObjectOutputStream out;
    /** Input socket stream. It is created in {@link ClientConnection#start()} method. */
    private ObjectInputStream in;

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

            Log.info(this, "Successfully connected to %1:%2", host, port);
        } catch (IOException e) {
            throw Log.error(this, new ChatIOException("Failed to open connection to %1:%2.", e, host, port));
        }
    }

    /**
     * Start listening to commands.
     */
    public void start() {
        checkState(state, ConnectionState.CREATED);

        try {
            // create streams for socket
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            // exception occurred while opening input stream
            Log.error(this, e, "I/O error occurred while opening socket streams.");

            // try to handle just in case, but this is not recoverable error
            occurredException = e;
            if (handler != null) {
                handler.canHandle(e);
            }
        }

        // start inner thread
        thread.start();

        state = ConnectionState.STARTED;

        Log.info(this, "Client connection %1:%2 successfully started.", socket.getInetAddress(), socket.getPort());
    }

    /**
     * Stop listening to commands, allows multiple call.
     */
    public void stop() {
        checkState(state, ConnectionState.STARTED, ConnectionState.STOP_REQUESED);

        state = ConnectionState.STOP_REQUESED;

        Log.info(this, "Client connection %1:%2 requested to stop.", socket.getInetAddress(), socket.getPort());
    }

    @Override
    public void run() {
        checkState(state, ConnectionState.STARTED);

        while (state != ConnectionState.STOP_REQUESED) {
            try {
                // TODO handle interrupt
                CommandHolder commandHolder = (CommandHolder) in.readObject();

                Log.debug(this, "Received command from %1:%2.", socket.getInetAddress(), socket.getPort());

                if (commandlistener != null) {
                    commandlistener.onCommand(this, commandHolder.getCommand());
                }
            } catch (IOException e) {
                occurredException = e;
            } catch (ClassNotFoundException e) {
                occurredException = e;
            } catch (ChatException e) {
                occurredException = e;
            } catch (RuntimeException e) {
                // need this, because unexpected runtime exception can occur
                occurredException = e;
            }

            if (occurredException != null) {
                Log.error(this, occurredException, "Error occurred while reading command from socket.");

                // try to handle exception
                if (handler != null && handler.canHandle(occurredException)) {
                    // exception is handled
                    occurredException = null;
                } else {
                    break;
                }
            }
        }

        state = ConnectionState.STOPPED;

        // close
        close();
    }

    /**
     * Send command over network. If command sending failed, connection will be closed.
     * 
     * @param command the command to send
     * @throws ChatIOException if I/O error occurred while sending
     */
    public void sendCommand(byte[] command) throws ChatIOException {
        checkState(state, ConnectionState.STARTED);

        try {
            CommandHolder commandHolder = new CommandHolder();
            commandHolder.setCommand(command);
            out.writeObject(commandHolder);
            out.flush();

            Log.debug(this, "Command sent to %1:%2.", socket.getInetAddress(), socket.getPort());
        } catch (IOException e) {
            Log.error(this, e, "Error occurred while sending command to %1:%2 socket.", socket.getInetAddress(), socket.getPort());

            // stop connection if failed to handle error
            if (handler == null || !handler.canHandle(e)) {
                occurredException = e;
                stop();
            }

            // anyway we throw exception to show that send failed
            throw new ChatIOException("Failed to send Command %1.", e, command);
        }
    }

    public void setOnCommandlistener(OnCommandListener commandlistener) {
        // TODO make thread safe
        this.commandlistener = commandlistener;
    }

    public void setOnCloseListener(OnClientCloseListener closeListener) {
        this.closeListener = closeListener;
    }

    /**
     * @return the tag
     */
    public Object getTag() {
        return tag;
    }

    /**
     * @param tag the tag to set
     */
    public void setTag(Object tag) {
        this.tag = tag;
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
            Log.warn(this, e, "Error occurred while closing socket.");
        }

        // call close listener
        if (closeListener != null) {
            closeListener.onClose(this, occurredException);
        }

        state = ConnectionState.CLOSED;
    }
    
    @Override
    public String toString() {
        return makeMessage("Client connection to '%1:%2'. State: %3.", socket.getInetAddress(), socket.getPort(), state);
    }
}
