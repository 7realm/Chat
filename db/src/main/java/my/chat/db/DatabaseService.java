package my.chat.db;

import static my.chat.commons.ArgumentHelper.checkInit;
import static my.chat.commons.ArgumentHelper.checkNotNull;
import static my.chat.commons.ArgumentHelper.checkString;

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import my.chat.model.User;

/**
 * Session Bean implementation class DatabaseService.
 * <p>
 * TODO do we need logging.
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
    public User login(String username, String password) throws PersistanceChatException {
        checkString("username", username);
        checkString("password", password);

        try {
            Query query = entityManager.createNamedQuery("loginUser", User.class)
                .setParameter("username", username)
                .setParameter("password", password)
                .setMaxResults(1);
            return (User) query.getSingleResult();
        } catch (NoResultException e) {
            throw new SecurityChatException("User '%1' is not found in database.", e, username);
        } catch (NonUniqueResultException e) {
            throw new SecurityChatException("More than one user with name '%1' is present in database.", e, username);
        } catch (PersistenceException e) {
            throw new PersistanceChatException("Failed to get user '%1' from database.", e, username);
        }
    }

    @Override
    public User createUser(String username, String password) throws PersistanceChatException {
        checkString("username", username);
        checkString("password", password);

        try {
            long count = (Long) entityManager.createNamedQuery("countUsersByName")
                .setParameter("username", username)
                .getSingleResult();

            if (count > 0) {
                throw new SecurityChatException("User %1 is already present in database.", username);
            }

            // create user
            User newUser = new User(username, password);
            entityManager.persist(newUser);
            entityManager.flush();
            return newUser;
        } catch (PersistenceException e) {
            throw new PersistanceChatException("Failed to insert user '%1' into database.", e, username);
        }
    }
    
    @Override
    public void updateUser(User user) throws PersistanceChatException {
        checkNotNull("user", user);

        try {
            entityManager.merge(user);
            entityManager.flush();
        } catch (PersistenceException e) {
            throw new PersistanceChatException("Failed to update user '%1' at database.", e, user.getUsername());
        }
    }
}
