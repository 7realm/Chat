package my.chat.client.console;

import static my.chat.commons.ArgumentHelper.checkNotNull;
import static my.chat.commons.Helper.makeMessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import my.chat.exceptions.ChatIOException;
import my.chat.model.Channel;
import my.chat.model.ChatMessage;
import my.chat.model.User;
import my.chat.network.ClientConnection;
import my.chat.network.Command;
import my.chat.network.Command.CommandType;
import my.chat.network.OnCommandListener;
import my.chat.parser.ParserChatException;
import my.chat.parser.ParserService;

public class ConsoleClient {
    private static final String CMD_LOGIN = "!login";
    private static final String CMD_JOIN = "!join";
    private static final String CMD_SEND = "!send";
    private static final String MARKER = "----------";
    private static final Object LOCK = new Object();
    private static final String CMD_LEAVE = "!leave";

    private static ClientConnection clientConnection;

    private static User currentUser;
    private static List<Channel> channels;
    private static Channel channel;
    private static int messageId;

    private static void showInfo() {
        System.out.println();

        if (currentUser == null) {
            System.out.println("User is not logged in.");
        } else {
            // print user name
            System.out.println(makeMessage("USER(%1): %2.", currentUser.getUserId(), currentUser.getUsername()));
            System.out.println(MARKER);

            // print contacts
            List<User> contacts = currentUser.getContacts();
            checkNotNull("contacts", contacts);
            if (contacts.size() == 0) {
                System.out.println("No contacts.");
            } else {
                for (User contact : contacts) {
                    System.out.println(makeMessage("%1 <%2>", contact.getUserId(), contact.getUsername()));
                }
            }
            System.out.println(MARKER);

            // print channels
            for (Channel channel : channels) {
                checkNotNull("channel.users", channel.getUsers());
                checkNotNull("channel.messages", channel.getMessages());
                System.out.println(makeMessage("Channel: %1 <%2>, users: %3, messages: %4.",
                    channel.getChannelId(), channel.getName(), channel.getUsers().size(), channel.getMessages().size()));

                // print users
                StringBuilder builder = new StringBuilder("Users: ");
                for (User user : channel.getUsers()) {
                    builder.append(user.getUsername()).append(" ");
                }
                System.out.println(builder.toString());

                // print messages
                for (ChatMessage message : channel.getMessages()) {
                    checkNotNull("message.author", message.getAuthor());

                    if (message.getReplyTo() == null) {
                        System.out.println(makeMessage("[%1] <%2>: %3",
                            message.getServerDate(), message.getAuthor().getUsername(), message.getContent()));
                    } else {
                        checkNotNull("message.replyTo.author", message.getReplyTo().getAuthor());

                        System.out.println(makeMessage("[%1] <%2>: %3\r\n       > %4: %5",
                            message.getServerDate(), message.getAuthor().getUsername(), message.getContent(),
                            message.getReplyTo().getAuthor().getUsername(), message.getReplyTo().getContent()));
                    }
                }

                System.out.println(MARKER);
            }
        }
    }

    public static void main(String[] args) throws ChatIOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        ParserService.getInstance().start();

        OnCommandListener commandlistener = new OnCommandListener() {
            @Override
            public void onCommand(ClientConnection connection, byte[] bytes) throws ChatIOException {
                try {
                    Command command = ParserService.getInstance().unmarshall(bytes);

                    switch (command.getType()) {
                    case CONNECTED:
                        currentUser = (User) command.get("user");
                        channels = (List<Channel>) command.get("publicChannels");
                        break;
                    case CHANNEL_JOIN:
                        Channel channel = (Channel) command.get("channel");

                        for (int i = 0; i < channels.size(); i++) {
                            if (channels.get(i).getChannelId() == channel.getChannelId()) {
                                channels.set(i, channel);
                                break;
                            }
                        }
                        break;
                    case CHANNEL_LEAVE:
                        long channelId = command.getLong("channelId");
                        long userId = command.getLong("userId");
                        for (int i = 0; i < channels.size(); i++) {
                            if (channels.get(i).getChannelId() == channelId) {
                                List<User> users = channels.get(i).getUsers();
                                for (Iterator<User> j = users.listIterator(); j.hasNext();) {
                                    if (j.next().getUserId() == userId) {
                                        j.remove();
                                        break;
                                    }
                                }
                                break;
                            }
                        }
                        break;
                    case CHANNEL_MESSAGE:
                        ChatMessage message = (ChatMessage) command.get("message");

                        channelId = message.getChannel().getChannelId();
                        for (int i = 0; i < channels.size(); i++) {
                            if (channels.get(i).getChannelId() == channelId) {
                                channels.get(i).getMessages().add(message);
                                break;
                            }
                        }
                    default:
                        break;
                    }

                } catch (ParserChatException e) {
                    System.err.println("Failed to process command.");
                    e.printStackTrace();
                } finally {
                    synchronized (LOCK) {
                        LOCK.notify();
                    }
                }
            }
        };

        while (true) {
            try {
                showInfo();

                String line = reader.readLine();
                if (line == null || line.length() == 0) {
                    break;
                }

                if (line.startsWith(CMD_LOGIN)) {
                    // default
                    String[] parts = line.split(" ", 3);
                    if (parts.length == 1) {
                        parts = new String[] { CMD_LOGIN, "ivern", "secret" };
                    }

                    clientConnection = new ClientConnection("localhost", 8844);
                    clientConnection.setOnCommandlistener(commandlistener);
                    clientConnection.start();

                    Command loginCommand = new Command(CommandType.LOGIN)
                        .addItem("username", parts[1])
                        .addItem("password", parts[2]);

                    send(loginCommand, true);
                } else {
                    if (line.startsWith(CMD_JOIN)) {
                        // default
                        String[] parts = line.split(" ", 2);
                        if (parts.length == 1) {
                            parts = new String[] { CMD_JOIN, "main" };
                        }

                        channel = getChannel(parts[1]);
                        if (channel == null) {
                            System.err.println("channel not found.");
                            break;
                        }

                        Command requestEnter = new Command(CommandType.CHANNEL_JOIN)
                            .addItem("userId", currentUser.getUserId())
                            .addItem("channelId", channel.getChannelId());

                        send(requestEnter, true);
                    } else if (line.startsWith(CMD_LEAVE)) {
                        if (channel == null) {
                            System.err.println("not in channel not found.");
                            break;
                        }

                        Command cmd = new Command(CommandType.CHANNEL_LEAVE)
                            .addItem("userId", currentUser.getUserId())
                            .addItem("channelId", channel.getChannelId());

                        send(cmd, true);
                    } else if (line.startsWith(CMD_SEND)) {
                        // default
                        String[] parts = line.split(" ", 2);
                        if (parts.length == 1) {
                            parts = new String[] { CMD_SEND, "Hello!" };
                        }

                        ChatMessage message = new ChatMessage(messageId++, currentUser, new Date(), parts[1], null, channel);

                        Command chatCommand = new Command(CommandType.CHANNEL_MESSAGE)
                            .addItem("message", message);

                        send(chatCommand, true);
                    } else if (line.startsWith("close")) {
                        clientConnection.stop();
                    } else {
                        System.out.println("Incorrect command.");
                    }
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (ParserChatException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        System.out.println("Shutdown.");
    }

    private static void send(Command command, boolean wait) throws ParserChatException, ChatIOException {
        if (clientConnection == null) {
            System.err.println("Client connection is not set.");
        } else {
            byte[] bytes = ParserService.getInstance().marshall(command);
            clientConnection.sendCommand(bytes);
        }
        if (wait) {
            synchronized (LOCK) {
                try {
                    LOCK.wait();
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    private static Channel getChannel(String name) {
        for (Channel channel : channels) {
            if (channel.getName().equals(name)) {
                return channel;
            }
        }
        return null;
    }

    private static Channel getChannel(long id) {
        for (Channel channel : channels) {
            if (channel.getChannelId() == id) {
                return channel;
            }
        }
        return null;
    }
}
