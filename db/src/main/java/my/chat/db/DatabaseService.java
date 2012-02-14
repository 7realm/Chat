package my.chat.db;

import static my.chat.commons.ArgumentHelper.checkInit;
import static my.chat.commons.ArgumentHelper.checkString;

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import my.chat.model.User;

/**
 * Session Bean implementation class DatabaseService
 */
@Stateful(mappedName = "db_service", name = "db_ejb")
public class DatabaseService implements DatabaseServiceRemote, DatabaseServiceLocal {
	@PersistenceContext(unitName = "model")
	private EntityManager entityManager;

	/**
	 * Default constructor.
	 */
	public DatabaseService() {
		// empty
	}

	@PostConstruct
	public void init() {
		checkInit("entityManager", entityManager);
	}

	@Override
	public User getUser(int id) {
		Query query = entityManager.createQuery("select * from user where iduser = ?").setParameter(1, id).setMaxResults(1);
		return (User) query.getSingleResult();
	}

	@Override
	public User login(String username, String password) throws LoginChatException {
		checkString("username", username);
		checkString("password", password);

		try {
			Query query = entityManager.createQuery("SELECT u FROM User u WHERE u.username = :username AND u.password = :password")
				.setParameter("username", username).setParameter("password", password).setMaxResults(1);
			return (User) query.getSingleResult();
		} catch (PersistenceException e) {
			// TODO: handle exception
			throw new LoginChatException("Failed to get user '" + username + "' from database.", e);
		}
	}
}
