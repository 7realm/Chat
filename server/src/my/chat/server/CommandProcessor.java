package my.chat.server;

import static my.chat.commons.ArgumentHelper.checkNotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import my.chat.db.SecurityChatException;
import my.chat.exceptions.ChatException;
import my.chat.exceptions.ChatIOException;
import my.chat.exceptions.ChatNotImplementedException;
import my.chat.logging.Log;
import my.chat.model.Channel;
import my.chat.model.Channel.ChannelType;
import my.chat.model.ChatMessage;
import my.chat.model.PrivateMessage;
import my.chat.model.user.User;
import my.chat.model.user.UserContact;
import my.chat.network.ClientConnection;
import my.chat.network.Command;
import my.chat.network.Command.CommandType;
import my.chat.network.CommandContentException;
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
    private int lastChannelId;

    public void start() {
        lastChannelId = 0;

        createChannel("main", ChannelType.PUBLIC);
        createChannel("support", ChannelType.PUBLIC);
    }

    public void acceptConnection(ClientConnection connection, User user) throws SecurityChatException {
        if (onlineUsers.containsKey(user.getUserId())) {
            throw new SecurityChatException("User %1 is already logged in.", user.getNickname());
        }

        // associate user with connection
        usersToConnection.put(user, connection);
        connection.setTag(user);
        onlineUsers.put(user.getUserId(), user);

        // reassign connection to server
        connection.setOnCloseListener(this);
        connection.setOnCommandlistener(this);
        connection.setExceptionHandler(this);

        // TODO init data here
        buildCommand(CommandType.LOGGED_IN)
            .addData("user", user)
            .addData("offlineMessages", new ArrayList<PrivateMessage>())
            .addData("publicChannels", new ArrayList<Channel>(channels.values()))
            .sendToUser(user);

        buildCommand(CommandType.USER_ENTER)
            .addData("user", user)
            .sendToAll();
    }

    protected Channel createChannel(String name, ChannelType type) {
        Channel channel = new Channel(lastChannelId++, name, type, new Date());
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

        try {
            User connectionUser = getUser(connection);

            switch (command.getType()) {
            case CHANNEL_JOIN:
                Channel channel = getChannel(command.getLong("channelId"));

                requestChannelEnter(channel, connectionUser);
                break;
            case CHANNEL_LEAVE:
                channel = getChannel(command.getLong("channelId"));

                removeUserFromChannel(channel, connectionUser);
                break;
            case CHANNEL_MESSAGE:
                ChatMessage chatMessage = (ChatMessage) command.get("message");
                checkValue(chatMessage.getChannel(), "CHANNEL_MESSAGE.message.channel");
                long id = chatMessage.getChannel().getChannelId();
                channel = getChannel(id);

                // set server data and add to messages
                chatMessage.setServerDate(new Date());
                chatMessage.setAuthor(connectionUser);
                channel.getMessages().add(chatMessage);

                // check if user is in channel
                if (channel.getUsers().contains(connectionUser)) {
                    buildCommand(CommandType.CHANNEL_MESSAGE)
                        .addData("message", chatMessage)
                        .sendToChannel(channel);
                } else {
                    sendFailure(command, connectionUser, "User is not in channel.");
                }
                break;
            case USER_ADD_CONTACT:
                User contactToAdd;
                if (command.contains("userId")) {
                    contactToAdd = getUser(command.getLong("userId"));
                } else if (command.contains("username")) {
                    contactToAdd = getUser(command.getString("username"));
                } else {
                    throw new CommandProcessorChatException("Command %1 should have userId or username.", command.getType());
                }

                if (connectionUser.getContacts().contains(contactToAdd)) {
                    sendFailure(command, connectionUser, "User is already present in contacts.");
                } else {
                    // add user to contacts
                    connectionUser.getContacts().add(new UserContact(contactToAdd.getNickname(), contactToAdd));

                    buildCommand(CommandType.USER_ADD_CONTACT)
                        .addData("user", contactToAdd)
                        .sendToUser(connectionUser);
                }
                break;
            case USER_REMOVE_CONTACT:
                long userId = command.getLong("userId");
                User contactToRemove = getUser(userId);
                if (connectionUser.getContacts().contains(contactToRemove)) {
                    for (Iterator<UserContact> i = connectionUser.getContacts().iterator(); i.hasNext();) {
                        UserContact userContact = i.next();
                        if (userId == userContact.getUser().getUserId()) {
                            i.remove();
                            break;
                        }

                    }

                    // remove user from contacts
                    connectionUser.getContacts().remove(contactToRemove);

                    buildCommand(CommandType.USER_REMOVE_CONTACT)
                        .addData("user", contactToRemove)
                        .sendToUser(connectionUser);
                } else {
                    sendFailure(command, connectionUser, "User is not present in contacts.");
                }
                break;
            case USER_MESSAGE:
                PrivateMessage privateMessage = (PrivateMessage) command.get("message");
                checkValue(privateMessage.getRecipient(), "USER_MESSAGE.message.recipient");

                // set server data and add to messages
                privateMessage.setServerDate(new Date());
                privateMessage.setAuthor(connectionUser);

                buildCommand(CommandType.USER_MESSAGE)
                    .addData("message", privateMessage)
                    .sendToUser(privateMessage.getRecipient());
                break;
            default:
                Log.warn(this, "Command %1 is ignored.", command.getType());
                break;
            }
        } catch (CommandContentException e) {
            // TODO we can send failure here for debug
            // problem with command content
            Log.error(this, e);
        } catch (CommandProcessorChatException e) {
            // TODO we can send failure here for debug
            // local exception, mainly because validation
            Log.error(this, e);
        }
    }

    @Override
    public boolean canHandle(Exception e) {
        return false;
    }

    protected void sendFailure(Command command, User user, String message) {
        buildCommand(CommandType.FAILURE)
            .addData("message", message)
            .addData("commandId", command.getId())
            .addData("commandType", command.getType())
            .sendToUser(user);
    }

    protected void sendCommandToUser(User user, Command command) {
        ClientConnection connection = usersToConnection.get(user);
        if (connection == null) {
            Log.warn(this, "User %1 has no connection.", user.getUserId());
        } else {
            sendCommand(connection, command);
        }
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

    private User getUser(long userId) throws CommandProcessorChatException {
        User user = onlineUsers.get(userId);
        if (user == null) {
            throw new CommandProcessorChatException("User is not present with ID %1.", userId);
        }
        return user;
    }

    private User getUser(ClientConnection connection) throws CommandProcessorChatException {
        User user = (User) connection.getTag();
        if (user == null) {
            throw new CommandProcessorChatException("User is not set for '%1'.", connection);
        }
        return user;
    }

    private Channel getChannel(long channelId) throws CommandProcessorChatException {
        Channel channel = channels.get(channelId);
        if (channel == null) {
            throw new CommandProcessorChatException("Channel is not present with ID %1.", channelId);
        }
        return channel;
    }

    private User getUser(String name) throws CommandProcessorChatException {
        for (User user : onlineUsers.values()) {
            if (user.getNickname().equalsIgnoreCase(name)) {
                return user;
            }
        }

        throw new CommandProcessorChatException("User is not present with name %1.", name);
    }

    private static void checkValue(Object value, String name) throws CommandProcessorChatException {
        if (value == null) {
            throw new CommandProcessorChatException("Value '%1' in command is not set.", name);
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
