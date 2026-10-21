package com.shopsphere.dao;

import com.shopsphere.model.CartItem;
import com.shopsphere.model.Product;
import com.shopsphere.util.DBConnectionUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CartDAO {
    private static final Logger LOGGER = Logger.getLogger(CartDAO.class.getName());

    public List<CartItem> findByUserId(int userId) {
        List<CartItem> list = new ArrayList<>();
        String sql = "SELECT c.cart_id, c.user_id, c.product_id, c.quantity, c.created_at, " +
                     "p.name, p.brand, p.price, p.discount, p.stock, p.image_url, p.status, cat.name AS category_name " +
                     "FROM cart c " +
                     "JOIN products p ON c.product_id = p.product_id " +
                     "JOIN categories cat ON p.category_id = cat.category_id " +
                     "WHERE c.user_id = ? ORDER BY c.cart_id DESC";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CartItem item = new CartItem(
                            rs.getInt("cart_id"),
                            rs.getInt("user_id"),
                            rs.getInt("product_id"),
                            rs.getInt("quantity"),
                            rs.getTimestamp("created_at")
                    );
                    Product prod = new Product();
                    prod.setProductId(rs.getInt("product_id"));
                    prod.setName(rs.getString("name"));
                    prod.setBrand(rs.getString("brand"));
                    prod.setPrice(rs.getBigDecimal("price"));
                    prod.setDiscount(rs.getBigDecimal("discount"));
                    prod.setStock(rs.getInt("stock"));
                    prod.setImageUrl(rs.getString("image_url"));
                    prod.setStatus(rs.getString("status"));
                    prod.setCategoryName(rs.getString("category_name"));
                    item.setProduct(prod);

                    list.add(item);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error in findByUserId cart: " + userId, e);
        }
        return list;
    }

    public Optional<CartItem> findByUserAndProduct(int userId, int productId) {
        String sql = "SELECT cart_id, user_id, product_id, quantity, created_at FROM cart WHERE user_id = ? AND product_id = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new CartItem(
                            rs.getInt("cart_id"),
                            rs.getInt("user_id"),
                            rs.getInt("product_id"),
                            rs.getInt("quantity"),
                            rs.getTimestamp("created_at")
                    ));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error in findByUserAndProduct", e);
        }
        return Optional.empty();
    }

    public boolean addItem(int userId, int productId, int quantity) {
        // Upsert logic: if item already in cart, increment quantity
        String sql = "INSERT INTO cart (user_id, product_id, quantity) VALUES (?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE quantity = quantity + VALUES(quantity)";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, productId);
            ps.setInt(3, quantity);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error adding item to cart", e);
        }
        return false;
    }

    public boolean updateQuantity(int cartId, int userId, int newQuantity) {
        if (newQuantity <= 0) {
            return removeItem(cartId, userId);
        }
        String sql = "UPDATE cart SET quantity = ? WHERE cart_id = ? AND user_id = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, newQuantity);
            ps.setInt(2, cartId);
            ps.setInt(3, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating cart quantity", e);
        }
        return false;
    }

    public boolean removeItem(int cartId, int userId) {
        String sql = "DELETE FROM cart WHERE cart_id = ? AND user_id = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cartId);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error removing item from cart", e);
        }
        return false;
    }

    public boolean clearCart(int userId) {
        String sql = "DELETE FROM cart WHERE user_id = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error clearing cart for userId: " + userId, e);
        }
        return false;
    }

    public boolean clearCart(Connection conn, int userId) throws SQLException {
        String sql = "DELETE FROM cart WHERE user_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
            return true;
        }
    }

    public int countCartItems(int userId) {
        String sql = "SELECT COALESCE(SUM(quantity), 0) FROM cart WHERE user_id = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error in countCartItems", e);
        }
        return 0;
    }
}
