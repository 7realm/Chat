package my.chat.client.console;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import my.chat.client.console.Instruction.InstructionType;
import my.chat.exceptions.ChatIOException;
import my.chat.parser.ParserChatException;
import my.chat.parser.ParserService;

public class ConsoleClient {
    public static void main(String[] args) {
        System.out.println("================== WELCOME TO CHAT ==================");
        System.out.println("HINT: use '!help' or '!h' to see list of commands.");
        System.out.println();

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        ParserService.getInstance().start();

        ClientCommandProcessor commandProcessor = new ClientCommandProcessor();

        // auto connect -> login -> join
        try {
            // connect
            if (ConfigManager.is(ConfigManager.PROP_AUTO_CONNECT)) {
                commandProcessor.processInstruction(Instruction.parse("!c"));

                // login
                if (ConfigManager.is(ConfigManager.PROP_AUTO_LOGIN)) {
                    commandProcessor.processInstruction(Instruction.parse("!l"));

                    // join main
                    if (ConfigManager.is(ConfigManager.PROP_AUTO_JOIN)) {
                        commandProcessor.processInstruction(Instruction.parse("!j"));
                    }
                }
            }
        } catch (InstructionChatException e) {
            System.err.println(e.getMessage());
        } catch (ParserChatException e) {
            e.printStackTrace();
        } catch (ChatIOException e) {
            e.printStackTrace();
        }

        while (true) {
            try {
                commandProcessor.showInfo();

                String line = reader.readLine();
                if (line == null) {
                    break;
                }

                // parse instruction from entered line
                Instruction instruction = Instruction.parse(line);

                commandProcessor.processInstruction(instruction);

                // specially handle quit command
                if (instruction.getType() == InstructionType.QUIT) {
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ChatIOException e) {
                e.printStackTrace();
            } catch (ParserChatException e) {
                e.printStackTrace();
            } catch (InstructionChatException e) {
                System.err.println(e.getMessage());
            }
        }

        System.out.println("Shutdown.");
    }
}
