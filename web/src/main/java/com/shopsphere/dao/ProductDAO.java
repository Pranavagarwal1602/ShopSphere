package com.shopsphere.dao;

import com.shopsphere.model.Product;
import com.shopsphere.util.DBConnectionUtil;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProductDAO {
    private static final Logger LOGGER = Logger.getLogger(ProductDAO.class.getName());

    public Optional<Product> findById(int productId) {
        String sql = "SELECT p.*, c.name AS category_name, " +
                     "COALESCE(AVG(r.rating), 0.0) AS avg_rating, COUNT(r.review_id) AS rev_count " +
                     "FROM products p " +
                     "JOIN categories c ON p.category_id = c.category_id " +
                     "LEFT JOIN reviews r ON p.product_id = r.product_id " +
                     "WHERE p.product_id = ? " +
                     "GROUP BY p.product_id, c.name";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToProduct(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error in findById product: " + productId, e);
        }
        return Optional.empty();
    }

    public List<Product> findFeatured(int limit) {
        return queryProductList(
                "SELECT p.*, c.name AS category_name, COALESCE(AVG(r.rating), 0.0) AS avg_rating, COUNT(r.review_id) AS rev_count " +
                "FROM products p JOIN categories c ON p.category_id = c.category_id " +
                "LEFT JOIN reviews r ON p.product_id = r.product_id " +
                "WHERE p.status = 'ACTIVE' " +
                "GROUP BY p.product_id, c.name ORDER BY p.product_id DESC LIMIT ?",
                limit
        );
    }

    public List<Product> findDiscounted(int limit) {
        return queryProductList(
                "SELECT p.*, c.name AS category_name, COALESCE(AVG(r.rating), 0.0) AS avg_rating, COUNT(r.review_id) AS rev_count " +
                "FROM products p JOIN categories c ON p.category_id = c.category_id " +
                "LEFT JOIN reviews r ON p.product_id = r.product_id " +
                "WHERE p.status = 'ACTIVE' AND p.discount > 0 " +
                "GROUP BY p.product_id, c.name ORDER BY p.discount DESC LIMIT ?",
                limit
        );
    }

    public List<Product> findTopRated(int limit) {
        return queryProductList(
                "SELECT p.*, c.name AS category_name, COALESCE(AVG(r.rating), 0.0) AS avg_rating, COUNT(r.review_id) AS rev_count " +
                "FROM products p JOIN categories c ON p.category_id = c.category_id " +
                "LEFT JOIN reviews r ON p.product_id = r.product_id " +
                "WHERE p.status = 'ACTIVE' " +
                "GROUP BY p.product_id, c.name ORDER BY avg_rating DESC, rev_count DESC LIMIT ?",
                limit
        );
    }

    public List<Product> searchAndFilter(String query, Integer categoryId, BigDecimal minPrice, BigDecimal maxPrice,
                                         String sort, int offset, int limit) {
        List<Product> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT p.*, c.name AS category_name, COALESCE(AVG(r.rating), 0.0) AS avg_rating, COUNT(r.review_id) AS rev_count " +
                "FROM products p JOIN categories c ON p.category_id = c.category_id " +
                "LEFT JOIN reviews r ON p.product_id = r.product_id " +
                "WHERE p.status = 'ACTIVE' "
        );
        List<Object> params = new ArrayList<>();

        if (query != null && !query.trim().isEmpty()) {
            sql.append("AND (p.name LIKE ? OR p.brand LIKE ? OR p.description LIKE ?) ");
            String q = "%" + query.trim() + "%";
            params.add(q);
            params.add(q);
            params.add(q);
        }

        if (categoryId != null && categoryId > 0) {
            sql.append("AND p.category_id = ? ");
            params.add(categoryId);
        }

        if (minPrice != null && minPrice.compareTo(BigDecimal.ZERO) >= 0) {
            sql.append("AND (p.price - p.discount) >= ? ");
            params.add(minPrice);
        }

        if (maxPrice != null && maxPrice.compareTo(BigDecimal.ZERO) > 0) {
            sql.append("AND (p.price - p.discount) <= ? ");
            params.add(maxPrice);
        }

        sql.append("GROUP BY p.product_id, c.name ");

        // Sorting
        if ("price_asc".equalsIgnoreCase(sort)) {
            sql.append("ORDER BY (p.price - p.discount) ASC ");
        } else if ("price_desc".equalsIgnoreCase(sort)) {
            sql.append("ORDER BY (p.price - p.discount) DESC ");
        } else if ("rating".equalsIgnoreCase(sort)) {
            sql.append("ORDER BY avg_rating DESC ");
        } else if ("name_asc".equalsIgnoreCase(sort)) {
            sql.append("ORDER BY p.name ASC ");
        } else {
            sql.append("ORDER BY p.product_id DESC ");
        }

        sql.append("LIMIT ? OFFSET ?");
        params.add(limit > 0 ? limit : 12);
        params.add(offset >= 0 ? offset : 0);

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToProduct(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error in searchAndFilter products", e);
        }
        return list;
    }

    public int countSearchAndFilter(String query, Integer categoryId, BigDecimal minPrice, BigDecimal maxPrice) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(DISTINCT p.product_id) FROM products p WHERE p.status = 'ACTIVE' ");
        List<Object> params = new ArrayList<>();

        if (query != null && !query.trim().isEmpty()) {
            sql.append("AND (p.name LIKE ? OR p.brand LIKE ? OR p.description LIKE ?) ");
            String q = "%" + query.trim() + "%";
            params.add(q);
            params.add(q);
            params.add(q);
        }
        if (categoryId != null && categoryId > 0) {
            sql.append("AND p.category_id = ? ");
            params.add(categoryId);
        }
        if (minPrice != null && minPrice.compareTo(BigDecimal.ZERO) >= 0) {
            sql.append("AND (p.price - p.discount) >= ? ");
            params.add(minPrice);
        }
        if (maxPrice != null && maxPrice.compareTo(BigDecimal.ZERO) > 0) {
            sql.append("AND (p.price - p.discount) <= ? ");
            params.add(maxPrice);
        }

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error in countSearchAndFilter", e);
        }
        return 0;
    }

    public List<Product> findAllForAdmin(String keyword, Integer categoryId) {
        List<Product> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT p.*, c.name AS category_name, 0.0 AS avg_rating, 0 AS rev_count " +
                "FROM products p JOIN categories c ON p.category_id = c.category_id WHERE 1=1 "
        );
        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (p.name LIKE ? OR p.brand LIKE ?) ");
            params.add("%" + keyword.trim() + "%");
            params.add("%" + keyword.trim() + "%");
        }
        if (categoryId != null && categoryId > 0) {
            sql.append("AND p.category_id = ? ");
            params.add(categoryId);
        }
        sql.append("ORDER BY p.product_id DESC");

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToProduct(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error in findAllForAdmin", e);
        }
        return list;
    }

    public List<Product> findLowStock(int threshold) {
        return queryProductList(
                "SELECT p.*, c.name AS category_name, 0.0 AS avg_rating, 0 AS rev_count " +
                "FROM products p JOIN categories c ON p.category_id = c.category_id " +
                "WHERE p.stock <= ? AND p.status = 'ACTIVE' ORDER BY p.stock ASC",
                threshold
        );
    }

    public boolean create(Product product) {
        String sql = "INSERT INTO products (category_id, name, brand, description, price, discount, stock, image_url, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, product.getCategoryId());
            ps.setString(2, product.getName());
            ps.setString(3, product.getBrand());
            ps.setString(4, product.getDescription());
            ps.setBigDecimal(5, product.getPrice());
            ps.setBigDecimal(6, product.getDiscount());
            ps.setInt(7, product.getStock());
            ps.setString(8, product.getImageUrl());
            ps.setString(9, product.getStatus() != null ? product.getStatus() : "ACTIVE");

            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        product.setProductId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating product: " + product.getName(), e);
        }
        return false;
    }

    public boolean update(Product product) {
        String sql = "UPDATE products SET category_id = ?, name = ?, brand = ?, description = ?, " +
                     "price = ?, discount = ?, stock = ?, image_url = ?, status = ? WHERE product_id = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, product.getCategoryId());
            ps.setString(2, product.getName());
            ps.setString(3, product.getBrand());
            ps.setString(4, product.getDescription());
            ps.setBigDecimal(5, product.getPrice());
            ps.setBigDecimal(6, product.getDiscount());
            ps.setInt(7, product.getStock());
            ps.setString(8, product.getImageUrl());
            ps.setString(9, product.getStatus());
            ps.setInt(10, product.getProductId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating product: " + product.getProductId(), e);
        }
        return false;
    }

    public boolean updateStock(int productId, int newStock) {
        String sql = "UPDATE products SET stock = ? WHERE product_id = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, newStock);
            ps.setInt(2, productId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating stock for productId: " + productId, e);
        }
        return false;
    }

    public boolean updateStockDelta(Connection conn, int productId, int delta) throws SQLException {
        // Safe atomic stock decrement / increment within transaction
        String sql = "UPDATE products SET stock = stock + ? WHERE product_id = ? AND (stock + ?) >= 0";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, delta);
            ps.setInt(2, productId);
            ps.setInt(3, delta);
            return ps.executeUpdate() > 0;
        }
    }

    public int countTotalProducts() {
        String sql = "SELECT COUNT(*) FROM products";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error in countTotalProducts", e);
        }
        return 0;
    }

    private List<Product> queryProductList(String sql, int param) {
        List<Product> list = new ArrayList<>();
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, param);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToProduct(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error in queryProductList: " + sql, e);
        }
        return list;
    }

    private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
        Product p = new Product(
                rs.getInt("product_id"),
                rs.getInt("category_id"),
                rs.getString("name"),
                rs.getString("brand"),
                rs.getString("description"),
                rs.getBigDecimal("price"),
                rs.getBigDecimal("discount"),
                rs.getInt("stock"),
                rs.getString("image_url"),
                rs.getString("status"),
                rs.getTimestamp("created_at")
        );
        p.setCategoryName(rs.getString("category_name"));
        try {
            p.setAverageRating(rs.getDouble("avg_rating"));
            p.setReviewCount(rs.getInt("rev_count"));
        } catch (SQLException ignored) {}
        return p;
    }
}
