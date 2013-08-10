package cats.chat.core.connection.event;

import cats.chat.core.connection.Connection;

/**
 * Josh
 * 09/08/13
 * 7:02 PM
 */
public interface ConnectionListener extends Listener{
    public void onClose(final Connection connection);
}
