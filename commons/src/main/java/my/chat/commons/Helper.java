package my.chat.commons;

import java.io.Closeable;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Helper {
    public static String makeMessage(String message, Object... params) {
        // TODO user string builder
        for (int i = 1; i <= params.length; i++) {
            message = message.replace("%" + i, String.valueOf(params[i - 1]));
        }
        return message;
    }

    public static void close(Closeable closeable) {
        try {
            closeable.close();
        } catch (IOException e) {
            // TODO ignore exceptions
        }
    }

    public static Properties loadProperties(String filePath, Properties defaults) throws IOException {
        Properties properties = defaults == null ? new Properties() : new Properties(defaults);
        InputStream input = null;
        try {
            input = new FileInputStream(filePath);
            properties.load(input);
            return properties;
        } catch (IllegalArgumentException e) {
            throw new IOException("Malformed unicode symbol in properties file '" + filePath + "'.", e);
        } finally {
            close(input);
        }
    }
}
