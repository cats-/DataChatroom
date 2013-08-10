package cats.chat.core.connection.event;

import cats.chat.core.connection.Connection;
import java.nio.ByteBuffer;

/**
 * Josh
 * 09/08/13
 * 7:02 PM
 */
public interface DataListener extends Listener{
    public void onData(final Connection connection, final ByteBuffer buffer);
}
