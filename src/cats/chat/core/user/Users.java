package cats.chat.core.user;

import java.nio.ByteBuffer;
import java.util.LinkedList;

/**
 * Josh
 * 09/08/13
 * 1:41 PM
 */
public class Users extends LinkedList<User> {

    public User user(final Object o){
        return stream().filter(u -> u.equals(o)).findFirst().orElse(null);
    }

    public void send(final ByteBuffer buffer){
        forEach(u -> u.connection.send(buffer));
    }
}
