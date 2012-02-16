package my.chat.server;

import static my.chat.commons.ArgumentHelper.checkNotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import my.chat.exceptions.ChatException;
import my.chat.exceptions.ChatIOException;
import my.chat.exceptions.ChatNotImplementedException;
import my.chat.model.Channel;
import my.chat.model.Channel.ChannelType;
import my.chat.model.ChatMessage;
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

    private final Map<User, ClientConnection> usersToConnection = Collections.synchronizedMap(new HashMap<User, ClientConnection>());
    private final Map<Long, Channel> channels = Collections.synchronizedMap(new HashMap<Long, Channel>());
    private final Map<Long, User> onlineUsers = Collections.synchronizedMap(new HashMap<Long, User>());
    private int channelId;

    public void start() {
        channelId = 0;

        createChannel("main", ChannelType.PUBLIC);
    }

    public void acceptConnection(ClientConnection connection, User user) {
        // associate user with connection
        usersToConnection.put(user, connection);
        connection.setTag(user);
        onlineUsers.put(user.getUserId(), user);

        // reassign connection to server
        connection.setOnCloseListener(this);
        connection.setOnCommandlistener(this);
        connection.setExceptionHandler(this);

        // TODO init data here
        buildCommand(CommandType.CONNECTED)
            .addData("user", user)
            .addData("offlineMessages", new ArrayList<PrivateMessage>())
            .addData("publicChannels", new ArrayList<Channel>(channels.values()))
            .sendToUser(user);

        buildCommand(CommandType.USER_ENTER)
            .addData("user", user)
            .sendToAll();
    }

    protected Channel createChannel(String name, ChannelType type) {
        Channel channel = new Channel(channelId++, name, type, new Date());
        channels.put(channel.getChannelId(), channel);
        return channel;
    }

    protected void requestChannelEnter(Channel channel, User user) {
        switch (channel.getType()) {
        case PUBLIC:
            addUserToChannel(channel, user);
            break;
        default:
            throw new ChatNotImplementedException("Channel type %1 is not implemented.", channel.getType());
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
        buildCommand(CommandType.CHANNEL_LEAVE)
            .addData("userId", user.getUserId())
            .addData("channelId", channel.getChannelId())
            .sendToChannel(channel);
        
        channel.getUsers().remove(user);
    }

    @Override
    public void onClose(ClientConnection connection, Exception occurredException) {
        // clear association
        User user = (User) connection.getTag();
        usersToConnection.remove(user);
        connection.setTag(null);
        onlineUsers.remove(user.getUserId());

        // remove from all channels
        for (Channel channel : channels.values()) {
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

        switch (command.getType()) {
        case CHANNEL_JOIN:
            User user = getUser(command.getLong("userId"));
            Channel channel = getChannel(command.getLong("channelId"));
            if (user == null) {
                // TODO remove sys err
                System.err.println("User is not found.");
                break;
            }
            if (channel == null) {
                System.err.println("Channel is not found.");
                break;
            }
            requestChannelEnter(channel, user);
            break;
        case CHANNEL_LEAVE:
            user = getUser(command.getLong("userId"));
            channel = getChannel(command.getLong("channelId"));
            if (user == null) {
                // TODO remove sys err
                System.err.println("User is not found.");
                break;
            }
            if (channel == null) {
                System.err.println("Channel is not found.");
                break;
            }
            removeUserFromChannel(channel, user);
            break;
        case CHANNEL_MESSAGE:
            // TODO check
            ChatMessage message = (ChatMessage) command.get("message");
            if (message == null) {
                System.err.println("Message is not found.");
                break;
            }
            long id = message.getChannel().getChannelId();
            channel = getChannel(id);
            if (channel == null) {
                System.err.println("Channel is not found.");
                break;
            }

            // set server data and add to messages
            message.setServerDate(new Date());
            channel.getMessages().add(message);

            buildCommand(CommandType.CHANNEL_MESSAGE)
                .addData("message", message)
                .sendToChannel(channel);
        default:
            break;
        }
    }

    @Override
    public boolean canHandle(Exception e) {
        // TODO Auto-generated method stub
        return false;
    }

    protected void sendCommandToUser(User user, Command command) {
        ClientConnection connection = usersToConnection.get(user);

        sendCommand(connection, command);
    }

    protected void sendCommandToChannel(Channel channel, Command command) {
        for (User user : channel.getUsers()) {
            sendCommandToUser(user, command);
        }
    }

    protected void sendCommandToAll(Command command) {
        for (Iterator<ClientConnection> i = usersToConnection.values().iterator(); i.hasNext();) {
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
            // TODO just ignore exceptions, logging is done at lower level
        } catch (ParserChatException e) {
            // TODO just ignore exceptions, logging is done at lower level
        }
    }

    private User getUser(long userId) {
        User user = onlineUsers.get(userId);
        return user;
    }

    private Channel getChannel(long channelId) {
        return channels.get(channelId);
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
