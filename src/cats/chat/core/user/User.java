package cats.chat.core.user;

import cats.chat.core.connection.Connection;

/**
 * Josh
 * 09/08/13
 * 1:38 PM
 */
public class User {

    public Connection connection;
    public String name;

    public boolean equals(final Object o){
        if(o == null)
            return false;
        if(o instanceof String)
            return name.equalsIgnoreCase((String)o);
        else if(o instanceof User)
            return equals(((User)o).name);
        else if(o instanceof Connection)
            return connection.equals(o);
        else
            return false;
    }

    public String toString(){
        return name;
    }
}
