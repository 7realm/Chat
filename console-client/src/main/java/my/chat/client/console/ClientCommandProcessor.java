package my.chat.client.console;

import static my.chat.commons.ArgumentHelper.checkNotNull;
import static my.chat.commons.Helper.makeMessage;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import my.chat.client.console.Instruction.InstructionType;
import my.chat.client.console.Instruction.Parameter;
import my.chat.exceptions.ChatIOException;
import my.chat.model.Channel;
import my.chat.model.ChatMessage;
import my.chat.model.user.User;
import my.chat.model.user.Contact;
import my.chat.network.command.Command;
import my.chat.network.command.Command.CommandType;
import my.chat.network.command.CommandContentException;
import my.chat.network.connection.client.ClientConnection;
import my.chat.network.connection.client.OnCommandListener;
import my.chat.parser.ParserChatException;
import my.chat.parser.ParserService;

public final class ClientCommandProcessor implements OnCommandListener {
    private static final String MARKER = "----------";
    private static final Object LOCK = new Object();

    private ClientConnection clientConnection;

    private User currentUser;
    /** List of known channels. TODO change to Map */
    private List<Channel> channels;
    private Channel currentChannel;
    private int messageId;

    public void processInstruction(Instruction instruction) throws InstructionChatException, ChatIOException, ParserChatException {
        switch (instruction.getType()) {
        case CONNECT:
            clientConnection = new ClientConnection(instruction.getParam(0), instruction.getIntParam(1));

            clientConnection.setOnCommandlistener(this);
            clientConnection.start();

            showInfo("Connected to %1:%2.", instruction.getParam(0), instruction.getIntParam(1));
            break;
        case LOGIN:
            Command loginCommand = new Command(CommandType.LOGIN)
                .addItem("username", instruction.getParam(0))
                .addItem("password", instruction.getParam(1));

            send(loginCommand, true);
            break;
        case JOIN:
            if (currentChannel != null) {
                throw new InstructionChatException("Already in channel %1.", currentChannel.getName());
            }
            Channel channel = getChannel(instruction.getParam(0));
            if (channel == null) {
                throw new InstructionChatException("Channel with name %1 does not exist.", instruction.getParam(0));
            }

            Command requestEnter = new Command(CommandType.CHANNEL_JOIN)
                .addItem("userId", currentUser.getId())
                .addItem("channelId", channel.getId());

            send(requestEnter, true);
            break;
        case EXIT:
            if (currentChannel == null) {
                throw new InstructionChatException("Not in channel.");
            }

            Command cmd = new Command(CommandType.CHANNEL_LEAVE)
                .addItem("userId", currentUser.getId())
                .addItem("channelId", currentChannel.getId());

            send(cmd, true);
            break;
        case SEND_CHAT:
            if (currentChannel == null) {
                throw new InstructionChatException("Not in channel.");
            }
            ChatMessage message = new ChatMessage(messageId++, currentUser, new Date(), instruction.getParam(0), null, currentChannel);
            Command chatCommand = new Command(CommandType.CHANNEL_MESSAGE)
                .addItem("message", message);

            send(chatCommand, true);
            break;
        case QUIT:
            if (clientConnection != null) {
                clientConnection.stop();
            }

            // in outer method return should be called
            break;
        case HELP:
            for (InstructionType type : Instruction.InstructionType.values()) {
                StringBuilder builder = new StringBuilder();
                for (Parameter parameter : type.getParameters()) {
                    builder.append("<").append(parameter.getInfo()).append(">").append(" ");
                }

                // append trailing spaces
                builder = new StringBuilder(makeMessage("%1,%2 %3:", type.getCommandLine0(), type.getCommandLine1(), builder.toString()));
                while (builder.length() < 30) {
                    builder.append(" ");
                }

                showInfo("%1%2", builder.toString(), type.getDescription());
            }
            break;
        default:
            throw new InstructionChatException("Instruction %1 is not supported.", instruction.getType());
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onCommand(ClientConnection connection, byte[] bytes) throws ChatIOException {
        try {
            Command command = ParserService.getInstance().unmarshall(bytes);

            switch (command.getType()) {
            case LOGGED_IN:
                currentUser = (User) command.get("user");
                channels = (List<Channel>) command.get("publicChannels");

                showInfo("Successfully logged in as %1.", currentUser.getNickname());
                break;
            case CHANNEL_JOIN:
                Channel channel = (Channel) command.get("channel");
                User user = (User) command.get("user");

                for (int i = 0; i < channels.size(); i++) {
                    if (channels.get(i).getId() == channel.getId()) {
                        channels.set(i, channel);

                        if (user.getId() == currentUser.getId()) {
                            // self join
                            showInfo("Joined %1.", channel.getName());
                            currentChannel = channel;
                        } else if (channel.getId() == currentChannel.getId()) {
                            // other user joined current channel
                            showInfo("User %1 joined.", currentChannel.getName());
                        }
                        break;
                    }
                }
                break;
            case CHANNEL_LEAVE:
                long channelId = command.getLong("channelId");
                long userId = command.getLong("userId");
                for (int i = 0; i < channels.size(); i++) {
                    if (channels.get(i).getId() == channelId) {
                        List<User> users = channels.get(i).getUsers();
                        for (Iterator<User> j = users.listIterator(); j.hasNext();) {
                            if (j.next().getId() == userId) {
                                j.remove();

                                if (userId == currentUser.getId()) {
                                    // self leave
                                    showInfo("Left %1.", channels.get(i).getName());
                                    currentChannel = null;
                                } else if (channelId == currentChannel.getId()) {
                                    // other user left current channel
                                    showInfo("User %1 joined.", currentChannel.getName());
                                }
                                break;
                            }
                        }
                        break;
                    }
                }
                break;
            case CHANNEL_MESSAGE:
                ChatMessage message = (ChatMessage) command.get("message");

                channelId = message.getChannel().getId();
                for (int i = 0; i < channels.size(); i++) {
                    if (channels.get(i).getId() == channelId) {
                        channels.get(i).getMessages().add(message);
                        break;
                    }
                }
                break;
            case FAILURE:
                showInfo("Command %1 failed: %2.", command.get("commandType"), command.getString("message"));
                break;
            default:
                break;
            }

        } catch (ParserChatException e) {
            System.err.println("Failed to process command.");
            e.printStackTrace();
        } catch (CommandContentException e) {
            System.err.println("Failed to process command.");
            e.printStackTrace();
        } finally {
            synchronized (LOCK) {
                LOCK.notify();
            }
        }
    }

    public void showInfo(String message, Object... params) {
        System.out.println(">>>>>>>>    " + makeMessage(message, params));
    }

    public void showInfo() {
        System.out.println();

        if (currentUser == null) {
            System.out.println("User is not logged in.");
        } else {
            // print user name
            System.out.println(makeMessage("USER(%1): %2.", currentUser.getId(), currentUser.getNickname()));
            System.out.println(MARKER);

            // print contacts
            List<Contact> contacts = currentUser.getContacts();
            checkNotNull("contacts", contacts);
            if (contacts.size() == 0) {
                System.out.println("No contacts.");
            } else {
                for (Contact contact : contacts) {
                    checkNotNull("contact.user", contact.getUser());
                    System.out.println(makeMessage("%1 <%2>", contact.getGivenName(), contact.getUser().getId()));
                }
            }
            System.out.println(MARKER);

            // print channels
            System.out.println("Public channels:");
            for (Channel channel : channels) {
                checkNotNull("channel.users", channel.getUsers());
                checkNotNull("channel.messages", channel.getMessages());
                System.out.println(makeMessage("Channel: %1 <%2>, users: %3, messages: %4.",
                    channel.getId(), channel.getName(), channel.getUsers().size(), channel.getMessages().size()));
            }
            System.out.println(MARKER);

            if (currentChannel != null) {
                System.out.println(makeMessage("CHANNEL: %1 <%2>, users: %3, messages: %4.",
                    currentChannel.getId(), currentChannel.getName(), currentChannel.getUsers().size(),
                    currentChannel.getMessages().size()));
                // print users
                StringBuilder builder = new StringBuilder("Users: ");
                for (User user : currentChannel.getUsers()) {
                    builder.append(user.getNickname()).append(" ");
                }
                System.out.println(builder.toString());

                // print messages
                for (ChatMessage message : currentChannel.getMessages()) {
                    checkNotNull("message.author", message.getAuthor());

                    if (message.getReplyTo() == null) {
                        System.out.println(makeMessage("[%1] <%2>: %3",
                            message.getServerDate(), message.getAuthor().getNickname(), message.getContent()));
                    } else {
                        checkNotNull("message.replyTo.author", message.getReplyTo().getAuthor());

                        System.out.println(makeMessage("[%1] <%2>: %3\r\n       > %4: %5",
                            message.getServerDate(), message.getAuthor().getNickname(), message.getContent(),
                            message.getReplyTo().getAuthor().getNickname(), message.getReplyTo().getContent()));
                    }
                }

                System.out.println(MARKER);
            }
        }
    }

    private void send(Command command, boolean wait) throws ParserChatException, ChatIOException {
        if (clientConnection == null) {
            System.err.println("Client connection is not set.");
        } else {
            byte[] bytes = ParserService.getInstance().marshall(command);
            clientConnection.sendCommand(bytes);
        }
        if (wait) {
            synchronized (LOCK) {
                try {
                    LOCK.wait(ConfigManager.getInt(ConfigManager.PROP_WAIT_TIMEOUT));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Channel getChannel(String name) {
        for (Channel channel : channels) {
            if (channel.getName().equals(name)) {
                return channel;
            }
        }
        return null;
    }
}
