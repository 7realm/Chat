package my.chat.client.console;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import my.chat.exceptions.ChatIOException;
import my.chat.network.ClientConnection;
import my.chat.network.Command;
import my.chat.network.Command.CommandType;
import my.chat.network.OnCommandListener;
import my.chat.parser.ParserChatException;
import my.chat.parser.ParserService;

public class ConsoleClient {
    private static ClientConnection clientConnection;

    public static void main(String[] args) throws ChatIOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        ParserService.getInstance().start();

        while (true) {
            try {
                String line = reader.readLine();
                if (line == null || line.length() == 0) {
                    break;
                }

                if (line.startsWith("login")) {
                    clientConnection = new ClientConnection("localhost", 8844);
                    clientConnection.setOnCommandlistener(new OnCommandListener() {
                        @Override
                        public void onCommand(ClientConnection connection, byte[] bytes) throws ChatIOException {
                            try {
                                Command xml = ParserService.getInstance().unmarshall(bytes);
                                System.out.println(xml.getType());
                            } catch (ParserChatException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    clientConnection.start();

                    String[] parts = line.split(" ");
                    Command loginCommand = new Command(CommandType.LOGIN).addItem("username", parts[1]).addItem("password", parts[2]);

                    byte[] bytes = ParserService.getInstance().marshall(loginCommand);
                    clientConnection.sendCommand(bytes);
                } else if (line.startsWith("send")) {
                    String[] parts = line.split(" ");

                    // Command chatCommand = new Command(CommandType. "me", parts[1]);
                    // clientConnection.sendCommand(chatCommand);
                } else if (line.equalsIgnoreCase("close")) {
                    clientConnection.stop();
                } else {
                    System.out.println("Incorrect command.");
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

}
