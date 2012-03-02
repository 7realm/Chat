package my.chat.model;

import my.chat.exceptions.ChatException;

public class UpdateChatException extends ChatException {
    private static final long serialVersionUID = -2356263422591698017L;

    /**
     * @param message
     * @param params
     */
    public UpdateChatException(String message, Object... params) {
        super(message, params);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     * @param cause
     * @param params
     */
    public UpdateChatException(String message, Throwable cause, Object... params) {
        super(message, cause, params);
        // TODO Auto-generated constructor stub
    }

}
