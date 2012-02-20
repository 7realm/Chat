package my.chat.server;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import my.chat.db.DatabaseService;
import my.chat.db.DatabaseServiceFactory;
import my.chat.exceptions.ChatException;
import my.chat.exceptions.ChatIOException;
import my.chat.model.user.User;
import my.chat.network.ClientConnection;
import my.chat.network.NetworkService;
import my.chat.parser.ParserService;
import my.chat.security.SecurityService;

public class Server {
    private final NetworkService networkService;
    private final SecurityService securityService;
    private final DatabaseService databaseService;

    private final Map<User, ClientConnection> users = Collections.synchronizedMap(new HashMap<User, ClientConnection>());

    public Server() throws ChatException {
        databaseService = DatabaseServiceFactory.getInstance();

        securityService = new SecurityService(databaseService);

        networkService = NetworkService.getInstance();
        networkService.setOnConnectionListener(securityService);
    }

    public void start() throws ChatIOException {
        networkService.start();
        CommandProcessor.getInstance().start();
        ParserService.getInstance().start();
    }

    private static Server server;

    public static Server getInstance() {
        return server;
    }

    public static void main(String[] args) throws ChatException {
        server = new Server();

        server.start();
        System.out.println("Server started.");
    }
}
