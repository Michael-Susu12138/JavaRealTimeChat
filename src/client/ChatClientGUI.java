package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.Socket;

public class ChatClientGUI {
	private JFrame frame;
    private JTextField messageField;
    private JTextArea chatArea;
    private JButton sendButton;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private Thread listenThread;
    private boolean running = true;
    private String userName;

    public ChatClientGUI() {
        userName = JOptionPane.showInputDialog(frame, "Enter your name:", "Username", JOptionPane.PLAIN_MESSAGE);
        if (userName == null || userName.isEmpty()) userName = "Anonymous"; // Default username
        initializeGUI();
        startConnection("127.0.0.1", 8000);
    }

    private void initializeGUI() {
        frame = new JFrame("Chat Client -- " + userName);
        frame.setSize(500, 400);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                sendMessage(userName + " has left the chat");
                stopConnection();
                frame.dispose();
            }
        });
        frame.setLayout(new BorderLayout());

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        frame.add(new JScrollPane(chatArea), BorderLayout.CENTER);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        messageField = new JTextField();
        panel.add(messageField, BorderLayout.CENTER);

        sendButton = new JButton("Send");
        panel.add(sendButton, BorderLayout.EAST);

        sendButton.addActionListener((ActionEvent e) -> {
            sendMessage(userName + ": " + messageField.getText());
            messageField.setText("");
        });

        frame.add(panel, BorderLayout.SOUTH);
        frame.setVisible(true);
    }

    private void startConnection(String ip, int port) {
        try {
            clientSocket = new Socket(ip, port);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            listenThread = new Thread(() -> {
                try {
                    String fromServer;
                    while (running && (fromServer = in.readLine()) != null) {
                        String finalMessage = fromServer;  // Local copy to be used inside lambda if necessary
                        SwingUtilities.invokeLater(() -> chatArea.append(finalMessage + "\n"));  // Ensure this is the line causing the issue
                    }
                } catch (IOException e) {
                    SwingUtilities.invokeLater(() -> chatArea.append("Error: " + e.getMessage() + "\n")); // Display errors on EDT
                }
            });
            listenThread.start();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Unable to connect to server: " + e.getMessage(), "Connection Error", JOptionPane.ERROR_MESSAGE);
        }
    }



    private void stopConnection() {
        running = false;
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (clientSocket != null) clientSocket.close();
            if (listenThread != null) listenThread.join();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String msg) {
        if (!msg.isEmpty()) {
            out.println(userName + ": " + msg); // Ensure the username is prefixed
            chatArea.append("Me: " + msg + "\n"); // Display the message in the chat area
            messageField.setText(""); // Clear the text field after sending
        }
    }

}
