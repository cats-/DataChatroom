package cats.chat.client;

import cats.chat.core.Constants;
import cats.chat.core.connection.Connection;
import cats.chat.core.connection.data.Data;
import cats.chat.core.connection.data.Opcodes;
import cats.chat.core.connection.event.DataListener;
import cats.chat.core.user.User;
import java.awt.BorderLayout;
import java.io.IOException;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

/**
 * Josh
 * 09/08/13
 * 2:49 PM
 */
public class Client extends JFrame implements Constants, Opcodes{

    private static final DateFormat TIME_FORMAT = new SimpleDateFormat("'['hh:mm:ss']'");

    private final JList<String> userList;
    private final DefaultListModel<String> userModel;
    private final JScrollPane userScroll;

    private final JList<String> messageList;
    private final DefaultListModel<String> messageModel;
    private final JScrollPane messageScroll;

    private final JLabel nameLabel;
    private final JTextField inputBox;
    private final JPanel inputPanel;

    private final User user;

    public Client(){
        super("Chat");
        setLayout(new BorderLayout());

        user = new User();

        userModel = new DefaultListModel<>();

        userList = new JList<>();
        userList.setModel(userModel);

        userScroll = new JScrollPane(userList);

        messageModel = new DefaultListModel<>();

        messageList = new JList<>();
        messageList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        messageList.setModel(messageModel);

        messageScroll = new JScrollPane(messageList);

        nameLabel = new JLabel();
        nameLabel.setBorder(new EmptyBorder(0, 5, 0, 5));
        nameLabel.setHorizontalAlignment(JLabel.CENTER);

        inputBox = new JTextField();
        inputBox.addActionListener(
                e -> {
                    final String text = inputBox.getText().trim();
                    if(text.isEmpty())
                        return;
                    user.connection.send(Data.create(MESSAGE, String.format("%s %s: %s", TIME_FORMAT.format(new Date()), user.name, text)));
                    inputBox.setText("");
                    inputBox.repaint();
                }
        );

        inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(nameLabel, BorderLayout.WEST);
        inputPanel.add(inputBox, BorderLayout.CENTER);

        final JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, messageScroll, userScroll);

        add(split, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);
    }

    public void start() throws IOException {
        user.connection = new Connection(new Socket(HOST, PORT));
        user.connection.addListener(
                (DataListener) (connection, buffer) -> {
                    final byte opcode = buffer.get();
                    final String data = Data.string(buffer);
                    switch(opcode){
                        case JOIN:
                            userModel.addElement(data);
                            break;
                        case LEAVE:
                            userModel.removeElement(data);
                            break;
                        case MESSAGE:
                            messageModel.addElement(data);
                            break;
                        case ASSIGN:
                            userModel.addElement(data);
                            user.name = data;
                            nameLabel.setText(data);
                            nameLabel.revalidate();
                            nameLabel.repaint();
                            inputPanel.revalidate();
                            inputPanel.repaint();
                            break;
                    }
                }
        );
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(500, 500);
        setVisible(true);
        setLocationRelativeTo(null);
    }

    public static void main(String args[]) throws IOException{
        new Client().start();
    }
}
