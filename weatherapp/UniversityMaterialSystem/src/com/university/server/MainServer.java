package com.university.server;

import java.net.*;
import java.io.*;
import java.util.concurrent.*;

public class MainServer {
    private static final int PORT = 8888;
    private static ExecutorService threadPool = Executors.newCachedThreadPool();
    private static volatile boolean running = true;
    
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║   University Material Sharing System  ║");
        System.out.println("║            SERVER STARTING             ║");
        System.out.println("╚════════════════════════════════════════╝");
        
        // Test database connection
        if (!DatabaseConnection.testConnection()) {
            System.err.println("Cannot start server without database connection!");
            return;
        }
        
        // Create uploads directory if not exists
        FileManager.initializeStorage();
        
        // Start server
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("\n✅ Server started successfully!");
            System.out.println("📡 Listening on port: " + PORT);
            System.out.println("👥 Waiting for client connections...\n");
            
            while (running) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("🔗 New client connected: " + clientSocket.getInetAddress());
                
                ClientHandler handler = new ClientHandler(clientSocket);
                threadPool.execute(handler);
            }
        } catch (IOException e) {
            System.err.println("❌ Server error: " + e.getMessage());
        }
    }
    
    public static void shutdown() {
        running = false;
        threadPool.shutdown();
        DatabaseConnection.closeConnection();
        System.out.println("Server shutting down...");
    }
}