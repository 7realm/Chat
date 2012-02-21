/**
 * 
 */
package my.chat.db.ejb;

import static my.chat.commons.ArgumentHelper.checkInit;

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
    private String jndiName;

    public EjbDatabaseServiceFactory() {
        // TODO Auto-generated constructor stub
    }

    public String getJndiName() {
        return jndiName;
    }

    public void setJndiName(String jndiName) {
        this.jndiName = jndiName;
    }
    
    protected void init() {
        checkInit("jndiName", jndiName);
    }

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

            return (DatabaseServiceRemote) context.lookup(jndiName);
        } catch (NamingException e) {
            throw new ChatException("Failed to initialize database service.", e);
        }
    }
}
