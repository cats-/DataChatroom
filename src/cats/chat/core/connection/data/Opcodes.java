package cats.chat.core.connection.data;

/**
 * Josh
 * 09/08/13
 * 12:46 PM
 */
public interface Opcodes {

    byte ASSIGN = 0x0;

    byte MESSAGE = 0x1;

    byte JOIN = 0x2;
    byte LEAVE = 0x3;
}
