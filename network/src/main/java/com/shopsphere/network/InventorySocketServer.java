package com.shopsphere.network;

import com.shopsphere.dto.InventoryDTO;
import com.shopsphere.util.DBConnectionUtil;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Multi-threaded TCP Socket Inventory Server built with java.net.ServerSocket.
 * Demonstrates:
 * - ServerSocket and Socket handling
 * - Custom text request/response protocol
 * - Serialization of InventoryDTO using ObjectOutputStream / ObjectInputStream
 * - Direct JDBC database access to MySQL
 */
public class InventorySocketServer {
    private static final Logger LOGGER = Logger.getLogger(InventorySocketServer.class.getName());
    public static final int DEFAULT_PORT = 8888;

    public static void main(String[] args) {
        int port = DEFAULT_PORT;
        if (args.length > 0) {
            try { port = Integer.parseInt(args[0]); } catch (Exception ignored) {}
        }

        LOGGER.info("Starting ShopSphere Inventory Socket Server on TCP Port: " + port);

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            LOGGER.info("Server is listening. Press Ctrl+C to stop.");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                LOGGER.info("Client connected from: " + clientSocket.getRemoteSocketAddress());
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Server encountered an IO exception: " + e.getMessage(), e);
        }
    }

    private static class ClientHandler implements Runnable {
        private final Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                 ObjectOutputStream objOut = new ObjectOutputStream(socket.getOutputStream())) {

                writer.println("200 SHOPSPHERE_INVENTORY_SERVER_READY");

                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if ("QUIT".equalsIgnoreCase(line) || "EXIT".equalsIgnoreCase(line)) {
                        writer.println("221 BYE");
                        break;
                    }

                    if (line.startsWith("GET_STOCK ")) {
                        handleGetStock(line, writer);
                    } else if (line.startsWith("GET_PRODUCT ")) {
                        handleGetProduct(line, writer, objOut);
                    } else if (line.startsWith("SEARCH ")) {
                        handleSearch(line, writer);
                    } else {
                        writer.println("400 UNKNOWN_COMMAND: Supported [GET_STOCK <id>, GET_PRODUCT <id>, SEARCH <q>, QUIT]");
                    }
                }
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Connection ended with client: " + e.getMessage());
            } finally {
                try { socket.close(); } catch (IOException ignored) {}
            }
        }

        private void handleGetStock(String cmd, PrintWriter writer) {
            try {
                int id = Integer.parseInt(cmd.substring("GET_STOCK ".length()).trim());
                try (Connection conn = DBConnectionUtil.getConnection();
                     PreparedStatement ps = conn.prepareStatement("SELECT stock, name FROM products WHERE product_id = ?")) {
                    ps.setInt(1, id);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            writer.println("200 STOCK=" + rs.getInt("stock") + " PRODUCT=" + rs.getString("name"));
                        } else {
                            writer.println("404 PRODUCT_NOT_FOUND");
                        }
                    }
                }
            } catch (Exception e) {
                writer.println("500 ERROR: " + e.getMessage());
            }
        }

        private void handleGetProduct(String cmd, PrintWriter writer, ObjectOutputStream objOut) {
            try {
                int id = Integer.parseInt(cmd.substring("GET_PRODUCT ".length()).trim());
                try (Connection conn = DBConnectionUtil.getConnection();
                     PreparedStatement ps = conn.prepareStatement(
                             "SELECT p.*, c.name AS cat_name FROM products p JOIN categories c ON p.category_id = c.category_id WHERE p.product_id = ?")) {
                    ps.setInt(1, id);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            InventoryDTO dto = new InventoryDTO(
                                    rs.getInt("product_id"),
                                    rs.getString("name"),
                                    rs.getString("brand"),
                                    rs.getString("cat_name"),
                                    rs.getBigDecimal("price"),
                                    rs.getInt("stock"),
                                    rs.getString("status")
                            );
                            writer.println("200 SERIALIZED_OBJECT_FOLLOWS");
                            objOut.writeObject(dto);
                            objOut.flush();
                        } else {
                            writer.println("404 PRODUCT_NOT_FOUND");
                        }
                    }
                }
            } catch (Exception e) {
                writer.println("500 ERROR: " + e.getMessage());
            }
        }

        private void handleSearch(String cmd, PrintWriter writer) {
            String q = cmd.substring("SEARCH ".length()).trim();
            try (Connection conn = DBConnectionUtil.getConnection();
                 PreparedStatement ps = conn.prepareStatement("SELECT product_id, name, stock, price FROM products WHERE name LIKE ? LIMIT 5")) {
                ps.setString(1, "%" + q + "%");
                try (ResultSet rs = ps.executeQuery()) {
                    StringBuilder sb = new StringBuilder("200 RESULTS: ");
                    boolean found = false;
                    while (rs.next()) {
                        found = true;
                        sb.append(String.format("[%d] %s (Stock: %d, ₹%.2f) | ",
                                rs.getInt("product_id"), rs.getString("name"), rs.getInt("stock"), rs.getBigDecimal("price")));
                    }
                    writer.println(found ? sb.toString() : "200 NO_ITEMS_MATCHED");
                }
            } catch (Exception e) {
                writer.println("500 ERROR: " + e.getMessage());
            }
        }
    }
}
