package com.shopsphere.network;

import com.shopsphere.dto.InventoryDTO;
import java.io.*;
import java.net.Socket;

/**
 * Client tester program for Socket communication.
 * Sends GET_STOCK, SEARCH, and GET_PRODUCT with ObjectInputStream deserialization.
 */
public class InventorySocketClient {
    public static void main(String[] args) {
        String host = "localhost";
        int port = InventorySocketServer.DEFAULT_PORT;

        System.out.println("Connecting to ShopSphere Inventory Server at " + host + ":" + port + "...");

        try (Socket socket = new Socket(host, port);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {

            // Read welcome greeting
            System.out.println("Server Greeting: " + reader.readLine());

            // 1. Send GET_STOCK
            System.out.println("\n--> Sending: GET_STOCK 1");
            writer.println("GET_STOCK 1");
            System.out.println("<-- Received: " + reader.readLine());

            // 2. Send SEARCH
            System.out.println("\n--> Sending: SEARCH Headphones");
            writer.println("SEARCH Headphones");
            System.out.println("<-- Received: " + reader.readLine());

            // 3. Send GET_PRODUCT and deserialize object
            System.out.println("\n--> Sending: GET_PRODUCT 1");
            writer.println("GET_PRODUCT 1");
            String status = reader.readLine();
            System.out.println("<-- Status: " + status);

            if (status != null && status.contains("SERIALIZED_OBJECT_FOLLOWS")) {
                ObjectInputStream objIn = new ObjectInputStream(socket.getInputStream());
                Object obj = objIn.readObject();
                if (obj instanceof InventoryDTO) {
                    InventoryDTO dto = (InventoryDTO) obj;
                    System.out.println(">>> Successfully Deserialized InventoryDTO: " + dto);
                }
            }

            // 4. Send QUIT
            System.out.println("\n--> Sending: QUIT");
            writer.println("QUIT");
            System.out.println("<-- Received: " + reader.readLine());
            System.out.println("Socket demonstration complete.");

        } catch (Exception e) {
            System.err.println("Client encountered error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
