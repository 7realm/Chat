package my.chat.db.ejb;

import javax.ejb.Remote;

import my.chat.db.DatabaseService;

@Remote
public interface DatabaseServiceRemote extends DatabaseService {
}
