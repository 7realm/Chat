package my.chat.db;
import javax.ejb.Remote;

import my.chat.model.User;

@Remote
public interface DatabaseServiceRemote {
	
	User getUser(int id);
	
	User login(String username, String password) throws PersistanceChatException;
	
	User createUser(String username, String password) throws PersistanceChatException;

    void updateUser(User user) throws PersistanceChatException;
}
