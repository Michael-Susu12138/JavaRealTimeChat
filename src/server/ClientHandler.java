package server;

import java.io.*;
import java.net.Socket;

public class ClientHandler extends Thread {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private ChatServer server;
    private volatile boolean running = true; // Control running state

    public ClientHandler(Socket socket, ChatServer server) {
        this.socket = socket;
        this.server = server;
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            while (running && !socket.isClosed()) {
                String inputLine = in.readLine();
                if (inputLine == null) break; // Stream closed, client disconnected
                server.broadcastMessage(inputLine, this);
            }
        } catch (IOException e) {
            System.out.println("ClientHandler Error: " + e.getMessage());
        } finally {
            closeResources();
            server.removeClient(this);
            server.broadcastMessage("Client disconnected", this);
        }
    }

    public void closeResources() {
        try {
            running = false;
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    public void stopHandler() {
        running = false; // Trigger shutdown
        closeResources();
    }
}
