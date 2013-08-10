package cats.chat.server;

import cats.chat.core.Constants;
import cats.chat.core.connection.Connection;
import cats.chat.core.connection.data.Data;
import cats.chat.core.connection.data.Opcodes;
import cats.chat.core.connection.event.ConnectionListener;
import cats.chat.core.connection.event.DataListener;
import cats.chat.core.user.User;
import cats.chat.core.user.Users;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Josh
 * 09/08/13
 * 1:44 PM
 */
public class Server extends Thread implements Runnable, Constants, Opcodes{

    private static final Users USERS = new Users();

    public Server(){
        setPriority(MAX_PRIORITY);
    }

    public void run() {
        try{
            final ServerSocket server = new ServerSocket(PORT);
            while(true){
                try{
                    final Socket socket = server.accept();
                    final Connection connection = new Connection(socket);
                    connection.addListener(
                            (ConnectionListener) c -> {
                                final User user = USERS.user(c);
                                USERS.remove(user);
                                USERS.send(Data.create(LEAVE, user.name));
                                USERS.send(Data.create(MESSAGE, String.format("%s has left the chat", user.name)));
                            }
                    );
                    connection.addListener(
                            (DataListener) (c, buffer) -> {
                                final byte opcode = buffer.get();
                                if(opcode == MESSAGE)
                                    USERS.send(Data.create(MESSAGE, Data.string(buffer)));
                            }
                    );
                    final User user = new User();
                    user.connection = connection;
                    user.name = "Guest" + System.currentTimeMillis();
                    connection.send(Data.create(ASSIGN, user.name));
                    connection.send(Data.create(MESSAGE, String.format("You are known as %s", user.name)));
                    USERS.forEach(u -> connection.send(Data.create(JOIN, u.name)));
                    USERS.send(Data.create(JOIN, user.name));
                    USERS.send(Data.create(MESSAGE, String.format("%s has just joined the chat", user.name)));
                    USERS.add(user);
                }catch(IOException ex){
                    ex.printStackTrace();
                }
            }
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }

    public static void main(String args[]){
        new Server().start();
    }
}
