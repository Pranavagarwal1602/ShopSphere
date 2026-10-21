package com.shopsphere.rmi;

import com.shopsphere.dto.InventoryDTO;
import com.shopsphere.util.DBConnectionUtil;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of the RMI Remote Interface.
 * Extends UnicastRemoteObject to establish remote TCP endpoint communication.
 */
public class InventoryRemoteServiceImpl extends UnicastRemoteObject implements InventoryRemoteService {

    private static final Logger LOGGER = Logger.getLogger(InventoryRemoteServiceImpl.class.getName());

    public InventoryRemoteServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public InventoryDTO getProduct(int productId) throws RemoteException {
        String sql = "SELECT p.*, c.name AS cat_name FROM products p JOIN categories c ON p.category_id = c.category_id WHERE p.product_id = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new InventoryDTO(
                            rs.getInt("product_id"),
                            rs.getString("name"),
                            rs.getString("brand"),
                            rs.getString("cat_name"),
                            rs.getBigDecimal("price"),
                            rs.getInt("stock"),
                            rs.getString("status")
                    );
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "RMI error in getProduct: " + e.getMessage(), e);
            throw new RemoteException("Database query failed: " + e.getMessage());
        }
        return null;
    }

    @Override
    public int getStock(int productId) throws RemoteException {
        String sql = "SELECT stock FROM products WHERE product_id = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("stock");
                }
            }
        } catch (Exception e) {
            throw new RemoteException("Database query failed: " + e.getMessage());
        }
        return -1;
    }

    @Override
    public boolean updateStock(int productId, int newQuantity) throws RemoteException {
        if (newQuantity < 0) {
            throw new RemoteException("Validation Error: Stock quantity cannot be negative.");
        }
        String sql = "UPDATE products SET stock = ? WHERE product_id = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, newQuantity);
            ps.setInt(2, productId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            throw new RemoteException("Database update failed: " + e.getMessage());
        }
    }
}
