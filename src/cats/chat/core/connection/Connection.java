package cats.chat.core.connection;

import cats.chat.core.connection.event.ConnectionListener;
import cats.chat.core.connection.event.DataListener;
import cats.chat.core.connection.event.Listener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

/**
 * Josh
 * 09/08/13
 * 12:02 PM
 */
public class Connection extends Thread implements Runnable{

    private final DataOutputStream output;
    private final DataInputStream input;
    private final Socket socket;

    private final List<Listener> listeners;

    public Connection(final Socket socket) throws IOException {
        this.socket = socket;

        listeners = new LinkedList<>();

        output = new DataOutputStream(socket.getOutputStream());
        output.flush();

        input = new DataInputStream(socket.getInputStream());

        setPriority(MAX_PRIORITY);
        start();
    }

    private void fireClose(){
        listeners.stream().filter(l -> l instanceof ConnectionListener).forEach(l -> ((ConnectionListener) l).onClose(this));
    }

    private void fireData(final ByteBuffer buffer){
        listeners.stream().filter(l -> l instanceof DataListener).forEach(l -> ((DataListener)l).onData(this, buffer));
    }

    public void addListener(final Listener l){
        listeners.add(l);
    }

    public boolean close(){
        try{
            output.close();
            input.close();
            socket.close();
            return true;
        }catch(IOException ex){
            ex.printStackTrace();
            return false;
        }
    }

    public void run(){
        while(true){
            try{
                final int length = input.readInt();
                final byte[] bytes = new byte[length];
                input.readFully(bytes);
                final ByteBuffer buffer = ByteBuffer.wrap(bytes);
                fireData(buffer);
            }catch(IOException ex){
                ex.printStackTrace();
                close();
                break;
            }
        }
        fireClose();
    }

    public boolean send(final ByteBuffer buffer){
        try{
            output.writeInt(buffer.capacity());
            output.write(buffer.array());
            output.flush();
            return true;
        }catch(IOException ex){
            ex.printStackTrace();
            return false;
        }
    }

}
