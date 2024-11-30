package com.example.demo;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class TcpServer {

    private static final int PORT = 8888;
    private static final int MAX_CLIENTS = 2;  // Maximum number of client connections
    private static final int WAIT_TIME = 6;   // Wait time (seconds)

    public static void main(String[] args) {
        // Use ThreadPoolExecutor to manage the thread pool
        ThreadPoolExecutor executorService = new ThreadPoolExecutor(
                MAX_CLIENTS, MAX_CLIENTS, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());

        BlockingQueue<Socket> waitingQueue = new LinkedBlockingQueue<>();  // Waiting queue
        ServerSocket serverSocket = null;

        try {
            // Create ServerSocket and bind it to the specified port
            serverSocket = new ServerSocket(PORT);
            System.out.println("=========== This is a MySQL server simulation ===========");
            System.out.println("Server started, waiting for client connections...");

            while (true) {
                Socket clientSocket = serverSocket.accept();  // Wait for a client connection

                if (executorService.getActiveCount() < MAX_CLIENTS) {
                    // If the current number of connections is less than the maximum, immediately handle the request
                    executorService.submit(new ClientHandler(clientSocket));
                } else {
                    // If the maximum connection count is reached, add the client to the waiting queue and start timeout check
                    System.out.println("Current connection limit reached, client is entering the waiting queue...");
                    long startTime = System.currentTimeMillis();  // Record the time when the client enters the queue
                    waitingQueue.put(clientSocket); // Add the client to the waiting queue

                    while (true) {
                        // Check every 100ms if there is a free thread
                        if (executorService.getActiveCount() < MAX_CLIENTS) {
                            Socket polledClient = waitingQueue.poll();  // Take a client from the queue
                            if (polledClient != null) {
                                System.out.println("A client from the waiting queue will be processed...");
                                executorService.submit(new ClientHandler(polledClient));
                                break;  // Exit the loop after processing the current client
                            }
                        }

                        // If the waiting time exceeds the maximum limit, discard the connection
                        if (System.currentTimeMillis() - startTime > WAIT_TIME * 1000) {
                            System.out.println("Connection request timed out, discarding client connection: " + clientSocket.getInetAddress());
                            clientSocket.close();  // Discard the client connection due to timeout
                            break;
                        }

                        // If the maximum connection count is reached but not yet timed out, continue checking the waiting queue
                        System.out.println("Waiting for an available processing slot...");
                        Thread.sleep(100);  // Small delay to avoid excessive CPU usage
                    }
                }
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                if (serverSocket != null) {
                    serverSocket.close();
                }
                executorService.shutdown();  // Shutdown the thread pool
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Client handler thread
    static class ClientHandler implements Runnable {
        private Socket clientSocket;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try (
                    BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    PrintWriter output = new PrintWriter(clientSocket.getOutputStream(), true)
            ) {
                String message;

                // Simulate server request processing by using sleep
                String initialResponse = "The server has received your message.";
                output.println(initialResponse);  // Reply to the client with a default message

                // Simulate processing time, e.g., sleep for 20 seconds
                System.out.println("Processing client request...");
                Thread.sleep(20000);  // Sleep for 20 seconds to simulate processing

                // Process the message sent by the client
                while ((message = input.readLine()) != null) {
                    System.out.println("Received message from client: " + message);
                    // Reply with a default response
                    String response = "The server has received your message: " + message + ", processing completed.";
                    output.println(response);
                    break;
                }

                // After processing the request, send an end message
                String endMessage = "END";
                output.println(endMessage);  // Send an end communication signal

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (clientSocket != null) {
                        clientSocket.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
