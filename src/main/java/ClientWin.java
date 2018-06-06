
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.util.Date;

public class ClientWin extends Thread {
    private JTextField messageField;
    private JButton sendButton;
    private JPanel jpanel;
    private JTextArea messagesBox;
    private JButton applyKeyButton;
    private JPasswordField keyField;
    private JTextField nicknameField;
    private JButton nicknameApplyButton;
    private JButton clearButton;
    private JButton connectButton;
    private JTextField hostField;
    private JTextField portField;
    private static JFrame J_Frame;

    private String key;
    private String nickname = "Guest";

    private Socket socket;

    DataInputStream inputStream;
    DataOutputStream outputStream;

    public ClientWin() throws IOException
    {

        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                start();
            }
        });
    }

    @Override
    public void run()
    {
        applyKeyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                key = keyField.getText();

                if(key.length() != 16)
                {
                    JOptionPane.showMessageDialog(J_Frame, "Key's length should be 16 symbols\nYour length: " + key.length());
                    key = "";
                }
                else
                    JOptionPane.showMessageDialog(J_Frame, "Key applied successfully");

            }
        });

        nicknameApplyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                nickname = nicknameField.getText();
            }
        });

        clearButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                messagesBox.setText(">> Connected to the server!\n");
            }
        });

        messageField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendButton.doClick();
            }
        });

        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(key != null && key.length() == 16)
                {
                    Date date = new Date();

                    String msg = ">> " + nickname + ": " + messageField.getText()+" | " + date.getHours()+":"+date.getMinutes()+"\n";

                    try {
                        outputStream.writeUTF(Encrypt.AESEncrypt(key, msg));
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }

                    messageField.setText("");
                }
                else if(key == null)
                    JOptionPane.showMessageDialog(J_Frame, "Your key field is empty");
                else if(key.length() != 16)
                    JOptionPane.showMessageDialog(J_Frame, "Key's length should be 16 symbols");
            }
        });

        try{
            socket = new Socket(hostField.getText(), Integer.parseInt(portField.getText()));

            showButtons();
            append(">> Connected to the server!");

            InputStream sin = socket.getInputStream();
            OutputStream sout = socket.getOutputStream();

            inputStream = new DataInputStream(sin);
            outputStream = new DataOutputStream(sout);

            while (true)
            {
                String message;

                message = inputStream.readUTF();

                if(!message.contains("/serverCommand"))
                {
                    append("\n" + Encrypt.AESDecrypt(key, message));
                    sleep(2000);
                    clear();
                }
                else
                    append("\n" + message.split("/serverCommand")[1]);

            }

        } catch (Exception e1) {
            clear();
            append(">> Unable to connect to the server.");

            try {
                Thread.sleep(2000);
                System.exit(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            e1.printStackTrace();
        }
    }

    public static void main(String[] args) throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException, IOException {
        JFrame frame = new JFrame("Chat");

        J_Frame = frame;

        frame.setContentPane(new ClientWin().jpanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(600, 600));

        frame.setLocationRelativeTo(null);

        frame.pack();
        frame.setVisible(true);
    }

    private void append(String s)
    {
        messagesBox.append(s);
    }

    private static void print(Object msg)
    {
        System.out.println(msg);
    }

    private static void printl(Object msg)
    {
        System.out.print(msg);
    }

    private void clear()
    {
        messagesBox.setText("");
        append(">> Connected to the server!");
    }

    private void hideButtons()
    {
        messagesBox.setEditable(false);
        messagesBox.setLineWrap(true);
        messagesBox.setWrapStyleWord(true);
        messagesBox.setFont(messagesBox.getFont().deriveFont(26f));
        messageField.setFont(messageField.getFont().deriveFont(21f));
        messageField.setVisible(false);
        keyField.setVisible(false);
        sendButton.setVisible(false);
        applyKeyButton.setVisible(false);
        clearButton.setVisible(false);
        nicknameApplyButton.setVisible(false);
        nicknameField.setVisible(false);
    }

    private void showButtons()
    {
        messageField.setVisible(true);
        keyField.setVisible(true);
        sendButton.setVisible(true);
        applyKeyButton.setVisible(true);
        clearButton.setVisible(true);
        nicknameApplyButton.setVisible(true);
        nicknameField.setVisible(true);
        connectButton.setVisible(false);
        hostField.setVisible(false);
        portField.setVisible(false);

        messagesBox.setEditable(false);
    }
}