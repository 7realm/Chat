package my.chat.commons;

import java.io.Closeable;
import java.io.IOException;

public class Helper {
    public static String makeMessage(String message, Object... params) {
        // TODO user string builder
        for (int i = 1; i <= params.length; i++) {
            message = message.replace("%"+ i, String.valueOf(params[i - 1]));
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
}
