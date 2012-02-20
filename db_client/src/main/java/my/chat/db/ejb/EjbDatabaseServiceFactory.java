/**
 * 
 */
package my.chat.db.ejb;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import my.chat.db.DatabaseService;
import my.chat.db.DatabaseServiceFactory;
import my.chat.exceptions.ChatException;

/**
 * 
 * <p>
 * <b>Thread safe:</b> No.
 * 
 * @author 7realm
 */
public class EjbDatabaseServiceFactory extends DatabaseServiceFactory {
    private static final String EJB_SERVICE_NAME = "db_service";

    /**
     * 
     * @return
     * @throws ChatException
     */
    @Override
    public DatabaseService buildService() throws ChatException {
        try {
            final Hashtable<String, String> jndiProperties = new Hashtable<String, String>();
            jndiProperties.put(Context.INITIAL_CONTEXT_FACTORY, "org.jnp.interfaces.NamingContextFactory");
            jndiProperties.put(Context.PROVIDER_URL, "localhost:1099");
            jndiProperties.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
            final Context context = new InitialContext(jndiProperties);

            return (DatabaseServiceRemote) context.lookup(EJB_SERVICE_NAME);
        } catch (NamingException e) {
            throw new ChatException("Failed to initialize database service.", e);
        }
    }
}
