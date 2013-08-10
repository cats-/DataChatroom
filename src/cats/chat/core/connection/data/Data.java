package cats.chat.core.connection.data;

import java.nio.ByteBuffer;

/**
 * Josh
 * 09/08/13
 * 2:14 PM
 */
public final class Data {

    private Data(){}

    public static ByteBuffer create(final byte opcode, final String string){
        final ByteBuffer buffer = ByteBuffer.allocate(1 + string.length());
        buffer.put(opcode);
        for(final char c : string.toCharArray())
            buffer.put((byte)c);
        return buffer;
    }

    public static String string(final ByteBuffer buffer){
        final StringBuilder builder = new StringBuilder();
        while(buffer.hasRemaining())
            builder.append((char)buffer.get());
        return builder.toString();
    }
}
