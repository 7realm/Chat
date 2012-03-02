/**
 * 
 */
package my.chat.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Date;
import java.util.List;

import my.chat.commands.CommandType;
import my.chat.model.user.Contact;
import my.chat.model.user.Credentials;
import my.chat.model.user.Status;
import my.chat.model.user.User;
import my.chat.network.command.Command;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test for {@link CommandType#USER_ENTER} command.
 * <p>
 * <b>Thread safe:</b> No.
 * 
 * @author 7realm
 */
public class UserEnterCommandTest {

    private static ParserService parserService = ParserService.getInstance();
    private User user0;
    private User user1;
    private User user2;
    private User user3;
    private User user4;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        parserService.start();
    }

    @Before
    public void setUp() {
        user0 = createUser(0);
        user1 = createUser(1, user0);
        user2 = createUser(2, user0, user1);
        user3 = createUser(3, user0, user1, user2);
        user4 = createUser(4, user0, user1, user2, user3);
    }

    /**
     * Tests marshaling/unmarshaling of full user.
     * 
     * @throws Exception
     */
    @Test
    public void testFullCommand() throws Exception {
        // prepare command
        Command inCommand = new Command(CommandType.USER_ENTER);
        inCommand.addItem("user", user4);

        byte[] bytes = parserService.marshall(inCommand);

        Command outCommand = parserService.unmarshall(bytes);

        User user = (User) outCommand.get("user");
        
        // remove contacts because there will be infinitive loop
        List<Contact> contacts = user.getContacts();
        u
        
        
        assertObjectsEquals("user", user4, user);
    }

    private static User createUser(long id, User... users) {
        User user = new User();
        user.setId(id);
        user.setNickname("user" + id);
        user.setCredentials(new Credentials("test_user" + id, "test_password" + id));
        user.getStatuses().add(new Status("old status of " + id, new Date(5000000)));
        user.getStatuses().add(new Status("new status of " + id, new Date(6000000)));
        for (User contactUser : users) {
            user.getContacts().add(new Contact(id + "_" + contactUser.getId(), contactUser));
        }
        return user;
    }

    private static void assertObjectsEquals(String entityName, Object expectedEntity, Object actualEntity) throws IllegalAccessException {
        assertNotNull("Expected entity " + entityName + " is null.", expectedEntity);
        assertNotNull("Actual entity " + entityName + " is null.", actualEntity);

        assertSame("Entities " + entityName + " are not of same class.", expectedEntity.getClass(), actualEntity.getClass());

        // check all private fields
        Class<?> clazz = expectedEntity.getClass();
        while (clazz.getSuperclass() != null) {
            for (Field field : clazz.getDeclaredFields()) {
                if (!Modifier.isStatic(field.getModifiers())) {
                    field.setAccessible(true);

                    String fieldName = entityName + "." + field.getName();
                    Object expectedFieldValue = field.get(expectedEntity);
                    Object actualFieldValue = field.get(actualEntity);
                    
                    assertSame("Fields " + entityName + " are not of same class.", expectedFieldValue, actualFieldValue);

                    // compare like primitive value types
                    if (expectedFieldValue instanceof Boolean || expectedFieldValue instanceof Number
                        || expectedFieldValue instanceof String) {
                        assertEquals("Values of " + fieldName + " are different.", expectedFieldValue, actualFieldValue);
                    } else if(expectedFieldValue instanceof List) {
                        List<?> expectedFieldList = (List<?>) expectedFieldValue;
                        List<?> actualFieldList = (List<?>) actualFieldValue;
                        
                        assertEquals("Lists at " + entityName + " has incorrect size.", expectedFieldList.size(), actualFieldList.size());
                    } else {
                        assertObjectsEquals(fieldName, expectedFieldValue, actualFieldValue);
                    }
                }
            }

            // need to handle all fields recursively
            clazz = clazz.getSuperclass();
        }
    }
}
