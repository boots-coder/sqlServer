package com.example.demo;

import java.io.*;
import java.net.*;

public class TcpClient1 {
    public static void main(String[] args) {
        try {
            // Assume the server's IP address is localhost and port is 8888
            String serverAddress = "localhost";  // Replace with the server's IP address
            int port = 8888;

            // Create a Socket and connect to the server
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(serverAddress, port), 3000);  // Set connection timeout to 3 seconds
            System.out.println("Connected to the server...");

            // Get input and output streams
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Send a fixed message "I am client 1"
            String message = "I am client 1";
            output.println(message);
            System.out.println("Message sent: " + message);

            // Continuously listen for the server's response until receiving the "END" signal
            String serverResponse;
            while (true) {
                serverResponse = input.readLine();  // Read the server's response

                if (serverResponse == null) {
                    System.out.println("Server closed the connection.");
                    break;
                }

                System.out.println("Server response: " + serverResponse);

                // Check if the "END" signal is received
                if ("END".equals(serverResponse)) {
                    System.out.println("Received end communication signal. Client will exit.");
                    break;  // Exit after receiving the end signal
                }
            }

            // Close the connection
            input.close();
            output.close();
            socket.close();
        } catch (SocketTimeoutException e) {
            System.out.println("Connection timed out, unable to connect to the server.");
        } catch (IOException e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }
}
