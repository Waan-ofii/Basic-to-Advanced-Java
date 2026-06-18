package services;

import java.io.*;
import java.net.*;
import java.util.function.Consumer;

public class NotificationClient implements Runnable {
    private String host;
    private int port;
    private int userId;
    private String role;
    private Consumer<String> onNotificationReceived;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private volatile boolean running = false;

    public NotificationClient(String host, int port, int userId, String role, Consumer<String> callback) {
        this.host = host;
        this.port = port;
        this.userId = userId;
        this.role = role;
        this.onNotificationReceived = callback;
    }

    // Constructor with default role (student)
    public NotificationClient(String host, int port, int userId, Consumer<String> callback) {
        this(host, port, userId, "student", callback);
    }

    @Override
    public void run() {
        running = true;
        try {
            socket = new Socket(host, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            // Register with server
            out.println("REG:" + userId + ":" + role);
            System.out.println("NotificationClient registered user: " + userId + " (" + role + ")");
            
            String line;
            while (running && (line = in.readLine()) != null) {
                if (onNotificationReceived != null) {
                    onNotificationReceived.accept(line);
                }
            }
        } catch (IOException e) {
            if (running) {
                System.out.println("Lost connection to notification server: " + e.getMessage());
            }
        } finally {
            disconnect();
        }
    }

    public void disconnect() {
        running = false;
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
