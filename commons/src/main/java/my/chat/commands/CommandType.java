package my.chat.commands;

/**
 * The enumeration of command types.
 * <p>
 * <b>Thread safe:</b> Yes.
 * 
 * @author 7realm
 */
public enum CommandType {
    /** Command for creating new account. */
    CREATE,
    
    /** For user login. */
    LOGIN,
    
    /** For user enter to chat. */
    USER_ENTER,

    /** For user exit from chat. */
    USER_EXIT,

    /** For private message between users. */
    USER_MESSAGE,

    /** Request to add user to contacts. */
    USER_ADD_CONTACT,

    /** Request to remove user from contacts. */
    USER_REMOVE_CONTACT,

    /** This command is sent when user successfully connected. */
    LOGGED_IN,

    /** User entered channel. */
    CHANNEL_JOIN,

    /** User left channel. */
    CHANNEL_LEAVE,

    /** For public message in chat channel. */
    CHANNEL_MESSAGE,

    /** General failure message. */
    FAILURE,
    
    /** Special command type that represents any command. It is used for parsing only. */
    ANY_COMMAND;
}