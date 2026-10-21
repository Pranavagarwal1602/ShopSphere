package com.shopsphere.dao;

import com.shopsphere.model.Coupon;
import com.shopsphere.util.DBConnectionUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CouponDAO {
    private static final Logger LOGGER = Logger.getLogger(CouponDAO.class.getName());

    public Optional<Coupon> findByCode(String code) {
        String sql = "SELECT coupon_id, code, discount_type, discount_value, minimum_order, maximum_discount, expiry_date, status " +
                     "FROM coupons WHERE UPPER(code) = UPPER(?)";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCoupon(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error in findByCode coupon: " + code, e);
        }
        return Optional.empty();
    }

    public List<Coupon> findAll() {
        List<Coupon> list = new ArrayList<>();
        String sql = "SELECT coupon_id, code, discount_type, discount_value, minimum_order, maximum_discount, expiry_date, status " +
                     "FROM coupons ORDER BY coupon_id DESC";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToCoupon(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error in findAll coupons", e);
        }
        return list;
    }

    public List<Coupon> findAvailableForCustomers() {
        List<Coupon> list = new ArrayList<>();
        String sql = "SELECT coupon_id, code, discount_type, discount_value, minimum_order, maximum_discount, expiry_date, status " +
                     "FROM coupons WHERE status = 'ACTIVE' AND expiry_date >= CURDATE() ORDER BY discount_value DESC";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToCoupon(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error in findAvailableForCustomers coupons", e);
        }
        return list;
    }

    public boolean create(Coupon coupon) {
        String sql = "INSERT INTO coupons (code, discount_type, discount_value, minimum_order, maximum_discount, expiry_date, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, coupon.getCode().trim().toUpperCase());
            ps.setString(2, coupon.getDiscountType());
            ps.setBigDecimal(3, coupon.getDiscountValue());
            ps.setBigDecimal(4, coupon.getMinimumOrder());
            ps.setBigDecimal(5, coupon.getMaximumDiscount());
            ps.setDate(6, coupon.getExpiryDate());
            ps.setString(7, coupon.getStatus() != null ? coupon.getStatus() : "ACTIVE");

            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        coupon.setCouponId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating coupon: " + coupon.getCode(), e);
        }
        return false;
    }

    public boolean update(Coupon coupon) {
        String sql = "UPDATE coupons SET discount_type = ?, discount_value = ?, minimum_order = ?, " +
                     "maximum_discount = ?, expiry_date = ?, status = ? WHERE coupon_id = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, coupon.getDiscountType());
            ps.setBigDecimal(2, coupon.getDiscountValue());
            ps.setBigDecimal(3, coupon.getMinimumOrder());
            ps.setBigDecimal(4, coupon.getMaximumDiscount());
            ps.setDate(5, coupon.getExpiryDate());
            ps.setString(6, coupon.getStatus());
            ps.setInt(7, coupon.getCouponId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating coupon: " + coupon.getCouponId(), e);
        }
        return false;
    }

    public boolean delete(int couponId) {
        String sql = "DELETE FROM coupons WHERE coupon_id = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, couponId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting coupon: " + couponId, e);
        }
        return false;
    }

    public boolean recordUsage(Connection conn, int couponId, int userId, int orderId) throws SQLException {
        String sql = "INSERT INTO coupon_usage (coupon_id, user_id, order_id) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, couponId);
            ps.setInt(2, userId);
            ps.setInt(3, orderId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean hasUserUsedCoupon(int couponId, int userId) {
        String sql = "SELECT 1 FROM coupon_usage WHERE coupon_id = ? AND user_id = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, couponId);
            ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error in hasUserUsedCoupon", e);
        }
        return false;
    }

    private Coupon mapResultSetToCoupon(ResultSet rs) throws SQLException {
        return new Coupon(
                rs.getInt("coupon_id"),
                rs.getString("code"),
                rs.getString("discount_type"),
                rs.getBigDecimal("discount_value"),
                rs.getBigDecimal("minimum_order"),
                rs.getBigDecimal("maximum_discount"),
                rs.getDate("expiry_date"),
                rs.getString("status")
        );
    }
}
