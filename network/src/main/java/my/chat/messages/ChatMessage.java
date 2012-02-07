package my.chat.messages;

import my.chat.network.Message;

public class ChatMessage extends Message {
	private static final long serialVersionUID = 2790989669052558913L;
	
	private String username;
	private String message;
	
	public ChatMessage(String username, String message) {
		super();
		this.username = username;
		this.message = message;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
}
