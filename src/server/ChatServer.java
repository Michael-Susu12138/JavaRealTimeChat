package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class ChatServer {
	 private ServerSocket serverSocket;
	    private Set<ClientHandler> clientHandlers = new HashSet<>();
	    private Thread serverThread;
	    private AtomicBoolean running = new AtomicBoolean(false);
	    private JTextArea logArea;  // GUI component to log messages

	    public ChatServer(JTextArea logArea) {
	        this.logArea = logArea;
	    }

	    public void start(int port) {
	        if (running.get()) {
	            return;
	        }
	        running.set(true);
	        serverThread = new Thread(() -> {
	            try {
	                serverSocket = new ServerSocket(port);
	                log("Server started on port " + port);
	                while (!serverSocket.isClosed()) {
	                    Socket clientSocket = serverSocket.accept();
	                    ClientHandler clientHandler = new ClientHandler(clientSocket, this);
	                    clientHandlers.add(clientHandler);
	                    clientHandler.start();
	                }
	            } catch (IOException e) {
	                log("Server exception: " + e.getMessage());
	            } finally {
	                stop();
	            }
	        });
	        serverThread.start();
	    }

	    public void stop() {
	        if (!running.get()) {
	            return;
	        }
	        try {
	            running.set(false);
	            for (ClientHandler client : clientHandlers) {
	                client.stopHandler();
	            }
	            if (!serverSocket.isClosed()) {
	                serverSocket.close();
	            }
	            log("Server stopped.");
	        } catch (IOException e) {
	            log("Error stopping server: " + e.getMessage());
	        }
	    }

	    public void log(String message) {
	        SwingUtilities.invokeLater(() -> logArea.append(message + "\n"));
	    }
    
    public void broadcastMessage(String message, ClientHandler sender) {
        for (ClientHandler client : clientHandlers) {
            if (client != sender) {
                client.sendMessage(message);
            }
        }
    }

    public void removeClient(ClientHandler clientHandler) {
        clientHandlers.remove(clientHandler);
        clientHandler.closeResources();  // Ensure resources are closed on client disconnect
    }

//    
//    public void stop() {
//        if (!running.get()) {
//            return;  // Server is not running
//        }
//        running.set(false);
//        try {
//            // Close all client sockets which will force ClientHandler threads to terminate
//            for (ClientHandler client : clientHandlers) {
//                client.closeResources();  // Close each client's resources
//            }
//            if (!serverSocket.isClosed()) {
//                serverSocket.close();  // Close server socket
//            }
//        } catch (IOException e) {
//            System.out.println("Error stopping the server: " + e.getMessage());
//        }
//    }


    public boolean isRunning() {
        return running.get() && !serverSocket.isClosed();
    }
}
