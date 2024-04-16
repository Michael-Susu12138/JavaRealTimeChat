package ChatApplication;



// TODO: 1. client connected needs to be renamed to its username
//       2. new client needs to be grayed out if server is not started yet
//       3. upgrade the fileIO is possible


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import server.ChatServer;
import client.ChatClientGUI;

public class ChatApplication {
    private JFrame frame;
    private JButton startServerButton, stopServerButton, launchClientButton;
    private JTextArea logArea;
    private ChatServer server; // Assuming ChatServer is your server class

    public ChatApplication() {
        initializeGUI();
    }

    private void initializeGUI() {
        frame = new JFrame("Chat Application Control Panel");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JPanel controlPanel = new JPanel();
        startServerButton = new JButton("Start Server");
        stopServerButton = new JButton("Stop Server");
        launchClientButton = new JButton("Launch Client");
        logArea = new JTextArea(10, 50);
        logArea.setEditable(false);

        controlPanel.add(startServerButton);
        controlPanel.add(stopServerButton);
        controlPanel.add(launchClientButton);

        frame.add(controlPanel, BorderLayout.NORTH);
        frame.add(new JScrollPane(logArea), BorderLayout.CENTER);

        startServerButton.addActionListener(e -> startServer());
        stopServerButton.addActionListener(e -> stopServer());
        launchClientButton.addActionListener(e -> launchClient());

        frame.setVisible(true);
    }

    private void startServer() {
        if (server == null || !server.isRunning()) {
            server = new ChatServer(logArea);  // Pass the log area to the server
            server.start(8000);
            log("Server started.");
        }
    }


    private void stopServer() {
        if (server != null && server.isRunning()) {
            server.stop();
            log("Server stopped.");
        }
    }

    private void launchClient() {
        SwingUtilities.invokeLater(() -> {
            new ChatClientGUI(); // Assuming this is your client class that sets up its own GUI
            log("New client launched.");
        });
    }

    private void log(String message) {
        logArea.append(message + "\n");
    }

    public static void main(String[] args) {
        new ChatApplication();
    }
}
