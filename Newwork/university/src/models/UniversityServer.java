package com.university.server;

import com.university.server.handlers.ClientHandler;
import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class UniversityServer {
    private static final int PORT = 8888;
    private static final int UDP_PORT = 8889;
    private static ExecutorService threadPool = Executors.newCachedThreadPool();
    private static volatile boolean running = true;
    
    public static void main(String[] args) {
        System.out.println("=== University Material Sharing System Server ===");
        System.out.println("Starting server on port " + PORT);
        
        // Start TCP Server
        startTCPServer();
        
        // Start UDP Server for broadcasts
        startUDPServer();
    }
    
    private static void startTCPServer() {
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(PORT)) {
                System.out.println("TCP Server listening on port " + PORT);
                
                while (running) {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("New client connected: " + clientSocket.getInetAddress());
                    
                    // Handle each client in a separate thread
                    ClientHandler handler = new ClientHandler(clientSocket);
                    threadPool.execute(handler);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
    
    private static void startUDPServer() {
        new Thread(() -> {
            try (DatagramSocket udpSocket = new DatagramSocket(UDP_PORT)) {
                System.out.println("UDP Server listening for broadcasts on port " + UDP_PORT);
                byte[] buffer = new byte[4096];
                
                while (running) {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    udpSocket.receive(packet);
                    
                    String message = new String(packet.getData(), 0, packet.getLength());
                    System.out.println("UDP Broadcast received: " + message);
                    
                    // Process UDP messages (notifications, announcements)
                    String response = processUDPRequest(message);
                    byte[] responseData = response.getBytes();
                    DatagramPacket responsePacket = new DatagramPacket(
                        responseData, responseData.length, 
                        packet.getAddress(), packet.getPort()
                    );
                    udpSocket.send(responsePacket);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
    
    private static String processUDPRequest(String message) {
        // Handle different UDP message types
        if (message.startsWith("ANNOUNCE:")) {
            String announcement = message.substring(9);
            broadcastToAllClients(announcement);
            return "ANNOUNCEMENT_BROADCAST: " + announcement;
        }
        return "ACK: " + message;
    }
    
    private static void broadcastToAllClients(String message) {
        // Implementation to broadcast to all connected TCP clients
        System.out.println("Broadcasting: " + message);
        // This would iterate through all active client handlers
    }
    
    public static void shutdown() {
        running = false;
        threadPool.shutdown();
        System.out.println("Server shutting down...");
    }
}