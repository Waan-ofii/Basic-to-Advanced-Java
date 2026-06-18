package services;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class NotificationServer {
    private static final int PORT = 5000;
    private static final Map<Integer, ClientHandler> clients = new ConcurrentHashMap<>();
    private static final List<ClientHandler> admins = new CopyOnWriteArrayList<>();
    private static ServerSocket serverSocket;
    private static boolean running = false;
    private static ExecutorService pool = Executors.newCachedThreadPool();

    public static void main(String[] args) {
        startServer();
    }

    public static synchronized void startServer() {
        if (running) return;
        running = true;
        
        Thread serverThread = new Thread(() -> {
            try {
                serverSocket = new ServerSocket(PORT);
                System.out.println("Notification Server started on port " + PORT);
                
                while (running) {
                    try {
                        Socket socket = serverSocket.accept();
                        ClientHandler handler = new ClientHandler(socket);
                        pool.execute(handler);
                    } catch (IOException e) {
                        if (!running) break;
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                stopServer();
            }
        });
        serverThread.setDaemon(true);
        serverThread.start();
    }

    public static synchronized void stopServer() {
        running = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        for (ClientHandler client : clients.values()) {
            client.disconnect();
        }
        clients.clear();
        admins.clear();
        pool.shutdown();
        System.out.println("Notification Server stopped.");
    }

    private static class ClientHandler implements Runnable {
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;
        private int userId = -1;
        private boolean isAdmin = false;

        public ClientHandler(Socket socket) {
            this.socket = socket;
            try {
                this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                this.out = new PrintWriter(socket.getOutputStream(), true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                String line;
                while ((line = in.readLine()) != null) {
                    String[] parts = line.split(":", 5);
                    if (parts.length < 2) continue;

                    String command = parts[0];
                    if (command.equals("REG")) {
                        // Registration: REG:userId:role
                        try {
                            this.userId = Integer.parseInt(parts[1]);
                            String role = parts.length > 2 ? parts[2] : "student";
                            if (role.equals("admin")) {
                                this.isAdmin = true;
                                admins.add(this);
                                System.out.println("Admin " + userId + " connected.");
                            } else {
                                clients.put(userId, this);
                                System.out.println("User " + userId + " connected.");
                            }
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    } else if (command.equals("SEND")) {
                        // Send single notif: SEND:targetUserId:title:message
                        if (parts.length >= 4) {
                            int targetId = Integer.parseInt(parts[1]);
                            String title = parts[2];
                            String msg = parts[3];
                            sendToUser(targetId, title + ": " + msg);
                        }
                    } else if (command.equals("BROADCAST")) {
                        // Broadcast notif: BROADCAST:title:message
                        if (parts.length >= 3) {
                            String title = parts[1];
                            String msg = parts[2];
                            broadcast(title + ": " + msg);
                        }
                    } else if (command.equals("ADMIN")) {
                        // Send notif to admins: ADMIN:title:message
                        if (parts.length >= 3) {
                            String title = parts[1];
                            String msg = parts[2];
                            sendToAdmins(title + ": " + msg);
                        }
                    }
                }
            } catch (IOException e) {
                // Client disconnected
            } finally {
                disconnect();
            }
        }

        public void sendMessage(String msg) {
            if (out != null) {
                out.println(msg);
            }
        }

        public void disconnect() {
            try {
                if (userId != -1) {
                    if (isAdmin) {
                        admins.remove(this);
                    } else {
                        clients.remove(userId);
                    }
                    System.out.println("User/Admin " + userId + " disconnected.");
                }
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void sendToUser(int userId, String msg) {
        ClientHandler handler = clients.get(userId);
        if (handler != null) {
            handler.sendMessage(msg);
        }
    }

    private static void sendToAdmins(String msg) {
        for (ClientHandler admin : admins) {
            admin.sendMessage(msg);
        }
    }

    private static void broadcast(String msg) {
        for (ClientHandler client : clients.values()) {
            client.sendMessage(msg);
        }
        for (ClientHandler admin : admins) {
            admin.sendMessage(msg);
        }
    }

    // Static helper method for other parts of the application to send notification alerts programmatically
    public static void sendSocketNotification(String command, String payload) {
        new Thread(() -> {
            try (Socket socket = new Socket("localhost", PORT);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
                out.println(command + ":" + payload);
            } catch (Exception e) {
                // Server might not be running, ignore socket send failure
            }
        }).start();
    }
}
