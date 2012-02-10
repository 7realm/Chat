package my.chat.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import my.chat.commands.ChatCommand;
import my.chat.commands.ConnectInfoCommand;
import my.chat.commands.UserEnterCommand;
import my.chat.commands.UserExitCommand;
import my.chat.commands.UserLeaveCommand;
import my.chat.exceptions.ChatException;
import my.chat.exceptions.ChatIOException;
import my.chat.model.Channel;
import my.chat.model.Channel.ChannelType;
import my.chat.model.PrivateMessage;
import my.chat.model.User;
import my.chat.network.ClientConnection;
import my.chat.network.Command;
import my.chat.network.ExceptionHandler;
import my.chat.network.NetworkService;
import my.chat.network.OnClientCloseListener;
import my.chat.network.OnCommandListener;

public final class CommandProcessor implements OnCommandListener, OnClientCloseListener, ExceptionHandler {
    private final static CommandProcessor INSTANCE = new CommandProcessor();

    private CommandProcessor() {
        // empty
    }

    public static CommandProcessor getInstance() {
        return INSTANCE;
    }

    private final Map<User, ClientConnection> users = Collections.synchronizedMap(new HashMap<User, ClientConnection>());
    private final List<Channel> channels = Collections.synchronizedList(new ArrayList<Channel>());
    private int channelId;

    public void start() {
        channelId = 0;

        createChannel("main", ChannelType.PUBLIC);
    }

    public void acceptConnection(ClientConnection connection, User user) {
        // associate user with connection
        users.put(user, connection);
        connection.setTag(user);

        // reassign connection to server
        connection.setOnCloseListener(this);
        connection.setOnCommandlistener(this);
        connection.setExceptionHandler(this);

        // TODO init data here
        sendCommand(connection, new ConnectInfoCommand(user, new ArrayList<PrivateMessage>(), channels));
        sendCommand(connection, new ChatCommand("server", "Welcome to Chat, " + user.getUsername() + "!"));

        sendCommandToAll(new UserEnterCommand(user));
    }

    protected Channel createChannel(String name, ChannelType type) {
        Channel channel = new Channel(channelId++, name, type, new Date());

        channels.add(channel);
        return channel;
    }

    protected void requestChannelEnter(Channel channel, User user) {
        
    }

    protected void removeUserFromChannel(Channel channel, User user) {
        channel.getUsers().remove(user);

        sendCommandToChannel(channel, new UserLeaveCommand(channel, user));
    }

    protected void sendCommandToUser(User user, Command command) {
        ClientConnection connection = users.get(user);

        sendCommand(connection, command);
    }

    protected void sendCommandToChannel(Channel channel, Command command) {
        for (User user : channel.getUsers()) {
            sendCommandToUser(user, command);
        }
    }

    protected void sendCommandToAll(Command command) {
        for (Iterator<ClientConnection> i = users.values().iterator(); i.hasNext();) {
            ClientConnection connection = i.next();

            sendCommand(connection, command);
        }
    }

    @Override
    public void onClose(ClientConnection connection, Exception occurredException) {
        // clear association
        User user = (User) connection.getTag();
        users.remove(user);
        connection.setTag(null);

        // remove from all channels
        for (Channel channel : channels) {
            if (channel.getUsers().contains(user)) {
                removeUserFromChannel(channel, user);
            }
        }
        
        sendCommandToAll(new UserExitCommand(user));
    }

    @Override
    public void onCommand(ClientConnection connection, Command command) throws ChatException {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean canHandle(Exception e) {
        // TODO Auto-generated method stub
        return false;
    }

    private void sendCommand(ClientConnection connection, Command command) {
        try {
            NetworkService.getInstance().sendCommand(connection, command);
        } catch (ChatIOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
