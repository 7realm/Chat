package my.chat.db;
import my.chat.model.User;

public interface DatabaseService {
	
	User getUser(int id);
	
	User login(String username, String password) throws PersistanceChatException;
	
	User createUser(String username, String password) throws PersistanceChatException;

    void updateUser(User user) throws PersistanceChatException;
}
