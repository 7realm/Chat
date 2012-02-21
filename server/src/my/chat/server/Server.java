package my.chat.server;

import my.chat.commons.FilePaths;
import my.chat.db.DatabaseService;
import my.chat.exceptions.ChatException;
import my.chat.exceptions.ChatIOException;
import my.chat.network.NetworkService;
import my.chat.parser.ParserService;
import my.chat.security.SecurityService;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class Server {
    private final NetworkService networkService;
    private final SecurityService securityService;
    private final DatabaseService databaseService;

    public Server() throws ChatException {
        try {
            // init application context
            ApplicationContext appContext = new FileSystemXmlApplicationContext(FilePaths.SERVICES_CONFIG);
            
            // create database service
            databaseService = (DatabaseService) appContext.getBean("databaseService");

            securityService = new SecurityService(databaseService);

            networkService = NetworkService.getInstance();
            networkService.setOnConnectionListener(securityService);
        } catch (ClassCastException e) {
            throw new ChatException("Failed to cast created service.", e);
        } catch (BeansException e) {
            throw new ChatException("Failed to create service beans.", e);
        }
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
