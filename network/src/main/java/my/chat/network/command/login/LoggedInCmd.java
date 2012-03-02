package my.chat.network.command.login;

import my.chat.commands.CommandType;
import my.chat.model.user.User;
import my.chat.network.command.BaseCommand;

public class LoggedInCmd extends BaseCommand {
    private User user;
    
    private List<Channel> publicChannels;
    

    @Override
    public CommandType getType() {
        return CommandType.LOGGED_IN;
    }

}
