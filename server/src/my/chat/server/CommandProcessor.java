package my.chat.server;

import static my.chat.commons.ArgumentHelper.checkNotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import my.chat.exceptions.ChatException;
import my.chat.exceptions.ChatIOException;
import my.chat.exceptions.ChatNotImplementedException;
import my.chat.model.Channel;
import my.chat.model.Channel.ChannelType;
import my.chat.model.PrivateMessage;
import my.chat.model.User;
import my.chat.network.ClientConnection;
import my.chat.network.Command;
import my.chat.network.Command.CommandType;
import my.chat.network.ExceptionHandler;
import my.chat.network.NetworkService;
import my.chat.network.OnClientCloseListener;
import my.chat.network.OnCommandListener;
import my.chat.parser.ParserChatException;
import my.chat.parser.ParserService;

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
        buildCommand(CommandType.CONNECTED)
            .addData("user", user)
            .addData("offlineMessages", new ArrayList<PrivateMessage>())
            .addData("publicChannels", channels)
            .sendToUser(user);
        
        buildCommand(CommandType.USER_ENTER)
            .addData("user", user)
            .sendToAll();
    }

    protected Channel createChannel(String name, ChannelType type) {
        Channel channel = new Channel(channelId++, name, type, new Date());
        channels.add(channel);
        return channel;
    }

    protected void requestChannelEnter(Channel channel, User user) {
        switch (channel.getType()) {
        case PUBLIC:
            addUserToChannel(channel, user);
            break;
        default:
            throw new ChatNotImplementedException("Channel type " + channel.getType() + " is not implemented.");
        }
    }

    protected void addUserToChannel(Channel channel, User user) {
        channel.getUsers().add(user);

        buildCommand(CommandType.CHANNEL_JOIN)
            .addData("user", user)
            .addData("channel", channel)
            .sendToChannel(channel);
    }

    protected void removeUserFromChannel(Channel channel, User user) {
        channel.getUsers().remove(user);

        buildCommand(CommandType.CHANNEL_LEAVE)
            .addData("userId", user.getUserId())
            .addData("channelId", channel.getChannelId())
            .sendToChannel(channel);
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

        buildCommand(CommandType.USER_EXIT)
            .addData("userId", user.getUserId())
            .sendToAll();
    }

    @Override
    public void onCommand(ClientConnection connection, byte[] bytes) throws ChatException {
        checkNotNull("connection", connection);
        checkNotNull("byte", bytes);

        Command command = ParserService.getInstance().unmarshall(bytes);
    }

    @Override
    public boolean canHandle(Exception e) {
        // TODO Auto-generated method stub
        return false;
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

    private void sendCommand(ClientConnection connection, Command command) {
        checkNotNull("connection", connection);
        checkNotNull("command", command);

        try {
            byte[] bytes = ParserService.getInstance().marshall(command);

            NetworkService.getInstance().sendCommand(connection, bytes);
        } catch (ChatIOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ParserChatException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private CommandBuilder buildCommand(CommandType type) {
        return new CommandBuilder(type);
    }

    public class CommandBuilder {
        private Command command;

        private CommandBuilder(CommandType type) {
            command = new Command(type);
        }

        private CommandBuilder addData(String name, Object value) {
            command.addItem(name, value);
            return this;
        }

        private void sendToAll() {
            sendCommandToAll(command);
        }

        private void sendToChannel(Channel channel) {
            sendCommandToChannel(channel, command);
        }

        private void sendToUser(User user) {
            sendCommandToUser(user, command);
        }

    }
}
