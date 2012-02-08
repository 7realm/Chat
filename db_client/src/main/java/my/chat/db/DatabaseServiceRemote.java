package my.chat.db;
import javax.ejb.Remote;

import my.chat.model.User;

@Remote
public interface DatabaseServiceRemote {
	
	User getUser(int id);
	
	User login(String username, String password) throws ChatLoginException;
}
