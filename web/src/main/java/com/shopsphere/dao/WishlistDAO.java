package com.shopsphere.dao;

import com.shopsphere.model.Product;
import com.shopsphere.model.WishlistItem;
import com.shopsphere.util.DBConnectionUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WishlistDAO {
    private static final Logger LOGGER = Logger.getLogger(WishlistDAO.class.getName());

    public List<WishlistItem> findByUserId(int userId) {
        List<WishlistItem> list = new ArrayList<>();
        String sql = "SELECT w.wishlist_id, w.user_id, w.product_id, w.created_at, " +
                     "p.name, p.brand, p.price, p.discount, p.stock, p.image_url, p.status, cat.name AS category_name " +
                     "FROM wishlist w " +
                     "JOIN products p ON w.product_id = p.product_id " +
                     "JOIN categories cat ON p.category_id = cat.category_id " +
                     "WHERE w.user_id = ? ORDER BY w.wishlist_id DESC";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    WishlistItem item = new WishlistItem(
                            rs.getInt("wishlist_id"),
                            rs.getInt("user_id"),
                            rs.getInt("product_id"),
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
            LOGGER.log(Level.SEVERE, "Error in findByUserId wishlist: " + userId, e);
        }
        return list;
    }

    public boolean add(int userId, int productId) {
        String sql = "INSERT IGNORE INTO wishlist (user_id, product_id) VALUES (?, ?)";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, productId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error adding to wishlist", e);
        }
        return false;
    }

    public boolean remove(int wishlistId, int userId) {
        String sql = "DELETE FROM wishlist WHERE wishlist_id = ? AND user_id = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, wishlistId);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error removing from wishlist", e);
        }
        return false;
    }

    public boolean exists(int userId, int productId) {
        String sql = "SELECT 1 FROM wishlist WHERE user_id = ? AND product_id = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, productId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking wishlist existence", e);
        }
        return false;
    }
}
