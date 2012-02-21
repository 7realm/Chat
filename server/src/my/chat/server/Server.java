package my.chat.server;

import my.chat.commons.FilePaths;
import my.chat.db.DatabaseService;
import my.chat.exceptions.ChatException;
import my.chat.exceptions.ChatIOException;
import my.chat.network.NetworkService;
import my.chat.parser.ParserService;
import my.chat.security.SecurityService;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

public class Server {
    private final NetworkService networkService;
    private final SecurityService securityService;
    private final DatabaseService databaseService;


    public Server() throws ChatException {
        try {
            // init services
            Resource resource = new FileSystemResource(FilePaths.SERVICES_CONFIG);
            BeanFactory factory = new XmlBeanFactory(resource);

            databaseService = (DatabaseService) factory.getBean("databaseService");

            // databaseService = DatabaseServiceFactory.getInstance();

            securityService = new SecurityService(databaseService);

            networkService = NetworkService.getInstance();
            networkService.setOnConnectionListener(securityService);

        } catch (ClassCastException e) {
            throw new ChatException("Failed to cast created service.", e);
        } catch (BeansException e) {
            throw new ChatException("Failed to create services.", e);
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
