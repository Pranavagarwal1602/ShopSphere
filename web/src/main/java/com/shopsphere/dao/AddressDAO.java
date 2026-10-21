package com.shopsphere.dao;

import com.shopsphere.model.Address;
import com.shopsphere.util.DBConnectionUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AddressDAO {
    private static final Logger LOGGER = Logger.getLogger(AddressDAO.class.getName());

    public List<Address> findByUserId(int userId) {
        List<Address> list = new ArrayList<>();
        String sql = "SELECT address_id, user_id, address_line, city, state, pincode, address_type, is_default, created_at " +
                     "FROM addresses WHERE user_id = ? ORDER BY is_default DESC, address_id DESC";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToAddress(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error in findByUserId addresses: " + userId, e);
        }
        return list;
    }

    public Optional<Address> findById(int addressId, int userId) {
        String sql = "SELECT address_id, user_id, address_line, city, state, pincode, address_type, is_default, created_at " +
                     "FROM addresses WHERE address_id = ? AND user_id = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, addressId);
            ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToAddress(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error in findById address: " + addressId, e);
        }
        return Optional.empty();
    }

    public Optional<Address> findDefaultByUserId(int userId) {
        String sql = "SELECT address_id, user_id, address_line, city, state, pincode, address_type, is_default, created_at " +
                     "FROM addresses WHERE user_id = ? AND is_default = TRUE LIMIT 1";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToAddress(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error in findDefaultByUserId: " + userId, e);
        }
        return Optional.empty();
    }

    public boolean create(Address address) {
        String sql = "INSERT INTO addresses (user_id, address_line, city, state, pincode, address_type, is_default) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnectionUtil.getConnection()) {
            if (address.isDefault()) {
                clearDefaultForUser(conn, address.getUserId());
            }
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, address.getUserId());
                ps.setString(2, address.getAddressLine());
                ps.setString(3, address.getCity());
                ps.setString(4, address.getState());
                ps.setString(5, address.getPincode());
                ps.setString(6, address.getAddressType() != null ? address.getAddressType() : "HOME");
                ps.setBoolean(7, address.isDefault());

                int rows = ps.executeUpdate();
                if (rows > 0) {
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) {
                            address.setAddressId(rs.getInt(1));
                        }
                    }
                    return true;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating address", e);
        }
        return false;
    }

    public boolean update(Address address) {
        String sql = "UPDATE addresses SET address_line = ?, city = ?, state = ?, pincode = ?, " +
                     "address_type = ?, is_default = ? WHERE address_id = ? AND user_id = ?";
        try (Connection conn = DBConnectionUtil.getConnection()) {
            if (address.isDefault()) {
                clearDefaultForUser(conn, address.getUserId());
            }
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, address.getAddressLine());
                ps.setString(2, address.getCity());
                ps.setString(3, address.getState());
                ps.setString(4, address.getPincode());
                ps.setString(5, address.getAddressType());
                ps.setBoolean(6, address.isDefault());
                ps.setInt(7, address.getAddressId());
                ps.setInt(8, address.getUserId());
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating address: " + address.getAddressId(), e);
        }
        return false;
    }

    public boolean delete(int addressId, int userId) {
        String sql = "DELETE FROM addresses WHERE address_id = ? AND user_id = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, addressId);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting address: " + addressId, e);
        }
        return false;
    }

    private void clearDefaultForUser(Connection conn, int userId) throws SQLException {
        String sql = "UPDATE addresses SET is_default = FALSE WHERE user_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        }
    }

    private Address mapResultSetToAddress(ResultSet rs) throws SQLException {
        return new Address(
                rs.getInt("address_id"),
                rs.getInt("user_id"),
                rs.getString("address_line"),
                rs.getString("city"),
                rs.getString("state"),
                rs.getString("pincode"),
                rs.getString("address_type"),
                rs.getBoolean("is_default"),
                rs.getTimestamp("created_at")
        );
    }
}
