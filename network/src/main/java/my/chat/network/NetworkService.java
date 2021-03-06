package my.chat.network;

import my.chat.exceptions.ChatException;
import my.chat.exceptions.ChatIOException;
import my.chat.exceptions.ExceptionHandler;
import my.chat.network.connection.client.ClientConnection;
import my.chat.network.connection.server.OnConnectionListener;
import my.chat.network.connection.server.OnServerCloseListener;
import my.chat.network.connection.server.ServerConnection;

public class NetworkService implements OnConnectionListener, OnServerCloseListener, ExceptionHandler {
    private static final NetworkService INSTANCE = new NetworkService();

    public static NetworkService getInstance() {
        return INSTANCE;
    }

    private NetworkService() {
        // empty
    }

    private ServerConnection serverConnection;

    private OnConnectionListener connectionListener;

    public void start() throws ChatIOException {
        serverConnection = new ServerConnection(NetworkConfig.SERVER_PORT);

        serverConnection.setOnConnectionListener(this);
        serverConnection.setOnCloseListener(this);

        serverConnection.start();
    }

    public void stop() {
        serverConnection.stop();
    }

    public void sendCommand(ClientConnection connection, byte[] bytes) throws ChatIOException {
        connection.sendCommand(bytes);
    }

    @Override
    public void onConnection(ClientConnection connection) throws ChatException {
        // assign connection to self and start it
        connection.start();

        // notify about connection
        if (connectionListener != null) {
            connectionListener.onConnection(connection);
        }
    }

    @Override
    public void onClose(ServerConnection connection, Exception occurredException) {
        // TODO network should be closed at this moment
    }

    @Override
    public boolean canHandle(Exception e) {
        return false;
    }

    public void setOnConnectionListener(OnConnectionListener listener) {
        connectionListener = listener;
    }
}
