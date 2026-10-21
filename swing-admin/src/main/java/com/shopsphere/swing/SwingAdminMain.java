package com.shopsphere.swing;

import com.shopsphere.model.Category;
import com.shopsphere.model.Product;
import com.shopsphere.model.User;
import com.shopsphere.util.DBConnectionUtil;
import com.shopsphere.util.PasswordUtil;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.*;
import java.util.Vector;

/**
 * Desktop Administration Dashboard built with Java Swing MVC.
 * Demonstrates RTU Syllabus topics:
 * - JFrame, JPanel, JTable, JScrollPane, JTabbedPane
 * - JLabel, JTextField, JPasswordField, JButton, JComboBox, JOptionPane
 * - Swing MVC event handling and JDBC connectivity to MySQL
 */
public class SwingAdminMain extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainCardPanel;

    // Login Components
    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JLabel lblLoginStatus;

    // Dashboard Table Models
    private DefaultTableModel productTableModel;
    private DefaultTableModel orderTableModel;
    private DefaultTableModel categoryTableModel;

    // Logged in admin
    private User currentAdmin;

    public SwingAdminMain() {
        setTitle("ShopSphere Desktop Administration Suite (RTU Capstone)");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Apply native system Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        cardLayout = new CardLayout();
        mainCardPanel = new JPanel(cardLayout);

        // Build Login Panel and Dashboard Panel
        mainCardPanel.add(buildLoginPanel(), "LOGIN");
        mainCardPanel.add(buildDashboardPanel(), "DASHBOARD");

        add(mainCardPanel);
        cardLayout.show(mainCardPanel, "LOGIN");
    }

    private JPanel buildLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(15, 23, 42)); // Deep Navy

        JPanel card = new JPanel(new GridLayout(6, 1, 10, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(30, 40, 30, 40)
        ));

        JLabel title = new JLabel("ShopSphere Desktop Admin", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(new Color(15, 23, 42));

        txtEmail = new JTextField("admin@shopsphere.com", 20);
        txtPassword = new JPasswordField("Admin@123", 20);

        JButton btnLogin = new JButton("Secure Login");
        btnLogin.setBackground(new Color(37, 99, 235));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnLogin.addActionListener(e -> attemptLogin());

        lblLoginStatus = new JLabel("Enter Administrator Credentials", SwingConstants.CENTER);
        lblLoginStatus.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblLoginStatus.setForeground(Color.GRAY);

        card.add(title);
        card.add(new JLabel("Email Address:"));
        card.add(txtEmail);
        card.add(new JLabel("Password:"));
        card.add(txtPassword);
        card.add(btnLogin);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(card, gbc);

        return panel;
    }

    private JPanel buildDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(15, 23, 42));
        header.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel brand = new JLabel("ShopSphere Enterprise Desktop Console");
        brand.setFont(new Font("Segoe UI", Font.BOLD, 16));
        brand.setForeground(Color.WHITE);

        JButton btnLogout = new JButton("Sign Out");
        btnLogout.addActionListener(e -> {
            currentAdmin = null;
            cardLayout.show(mainCardPanel, "LOGIN");
        });

        header.add(brand, BorderLayout.WEST);
        header.add(btnLogout, BorderLayout.EAST);
        panel.add(header, BorderLayout.NORTH);

        // Tabbed Pane for Products, Orders, Categories
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));

        tabbedPane.addTab("Product Inventory", buildProductsTab());
        tabbedPane.addTab("Customer Orders", buildOrdersTab());
        tabbedPane.addTab("Categories", buildCategoriesTab());

        panel.add(tabbedPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildProductsTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnRefresh = new JButton("Refresh Products");
        JButton btnUpdateStock = new JButton("Quick Stock Update");

        btnRefresh.addActionListener(e -> loadProductsData());
        btnUpdateStock.addActionListener(e -> quickStockUpdate());

        toolbar.add(btnRefresh);
        toolbar.add(btnUpdateStock);
        panel.add(toolbar, BorderLayout.NORTH);

        // JTable
        String[] cols = {"ID", "Product Name", "Brand", "Price (₹)", "Discount", "Stock", "Status"};
        productTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable table = new JTable(productTableModel);
        table.setRowHeight(24);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        return panel;
    }

    private JPanel buildOrdersTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnRefreshOrders = new JButton("Refresh Orders");
        JButton btnUpdateStatus = new JButton("Mark Delivered / Cancel");

        btnRefreshOrders.addActionListener(e -> loadOrdersData());
        btnUpdateStatus.addActionListener(e -> updateOrderStatusDialog());

        toolbar.add(btnRefreshOrders);
        toolbar.add(btnUpdateStatus);
        panel.add(toolbar, BorderLayout.NORTH);

        String[] cols = {"Order ID", "User ID", "Total (₹)", "Payment", "Payment Status", "Order Status", "Placed Date"};
        orderTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable table = new JTable(orderTableModel);
        table.setRowHeight(24);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        return panel;
    }

    private JPanel buildCategoriesTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnRefreshCats = new JButton("Refresh Categories");
        btnRefreshCats.addActionListener(e -> loadCategoriesData());
        toolbar.add(btnRefreshCats);
        panel.add(toolbar, BorderLayout.NORTH);

        String[] cols = {"Category ID", "Category Name", "Description", "Status"};
        categoryTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable table = new JTable(categoryTableModel);
        table.setRowHeight(24);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        return panel;
    }

    private void attemptLogin() {
        String email = txtEmail.getText().trim();
        String pass = new String(txtPassword.getPassword());

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT user_id, name, email, password, role, status FROM users WHERE email = ?")) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String role = rs.getString("role");
                    String hashed = rs.getString("password");
                    if ("ADMIN".equalsIgnoreCase(role) && PasswordUtil.checkPassword(pass, hashed)) {
                        currentAdmin = new User();
                        currentAdmin.setUserId(rs.getInt("user_id"));
                        currentAdmin.setName(rs.getString("name"));
                        currentAdmin.setEmail(rs.getString("email"));
                        currentAdmin.setRole(role);

                        cardLayout.show(mainCardPanel, "DASHBOARD");
                        loadProductsData();
                        loadOrdersData();
                        loadCategoriesData();
                        return;
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Database Connection Error: " + e.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(this, "Invalid credentials or non-admin user role.", "Access Denied", JOptionPane.WARNING_MESSAGE);
    }

    private void loadProductsData() {
        productTableModel.setRowCount(0);
        try (Connection conn = DBConnectionUtil.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT product_id, name, brand, price, discount, stock, status FROM products ORDER BY product_id DESC")) {
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("product_id"));
                row.add(rs.getString("name"));
                row.add(rs.getString("brand"));
                row.add(rs.getBigDecimal("price"));
                row.add(rs.getBigDecimal("discount"));
                row.add(rs.getInt("stock"));
                row.add(rs.getString("status"));
                productTableModel.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load products: " + e.getMessage());
        }
    }

    private void loadOrdersData() {
        orderTableModel.setRowCount(0);
        try (Connection conn = DBConnectionUtil.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT order_id, user_id, total_amount, payment_method, payment_status, order_status, created_at FROM orders ORDER BY order_id DESC")) {
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("order_id"));
                row.add(rs.getInt("user_id"));
                row.add(rs.getBigDecimal("total_amount"));
                row.add(rs.getString("payment_method"));
                row.add(rs.getString("payment_status"));
                row.add(rs.getString("order_status"));
                row.add(rs.getTimestamp("created_at"));
                orderTableModel.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load orders: " + e.getMessage());
        }
    }

    private void loadCategoriesData() {
        categoryTableModel.setRowCount(0);
        try (Connection conn = DBConnectionUtil.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT category_id, name, description, status FROM categories ORDER BY category_id ASC")) {
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("category_id"));
                row.add(rs.getString("name"));
                row.add(rs.getString("description"));
                row.add(rs.getString("status"));
                categoryTableModel.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load categories: " + e.getMessage());
        }
    }

    private void quickStockUpdate() {
        String idStr = JOptionPane.showInputDialog(this, "Enter Product ID to update stock:");
        if (idStr == null || idStr.trim().isEmpty()) return;

        String stockStr = JOptionPane.showInputDialog(this, "Enter New Stock Quantity:");
        if (stockStr == null || stockStr.trim().isEmpty()) return;

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement("UPDATE products SET stock = ? WHERE product_id = ?")) {
            ps.setInt(1, Integer.parseInt(stockStr.trim()));
            ps.setInt(2, Integer.parseInt(idStr.trim()));
            int rows = ps.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Product Stock updated successfully!");
                loadProductsData();
            } else {
                JOptionPane.showMessageDialog(this, "Product ID not found.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error updating stock: " + e.getMessage());
        }
    }

    private void updateOrderStatusDialog() {
        String idStr = JOptionPane.showInputDialog(this, "Enter Order ID:");
        if (idStr == null || idStr.trim().isEmpty()) return;

        String[] statuses = {"PLACED", "CONFIRMED", "SHIPPED", "DELIVERED", "CANCELLED"};
        String selected = (String) JOptionPane.showInputDialog(this, "Select New Order Status:",
                "Order Status", JOptionPane.QUESTION_MESSAGE, null, statuses, statuses[3]);

        if (selected == null) return;

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement("UPDATE orders SET order_status = ? WHERE order_id = ?")) {
            ps.setString(1, selected);
            ps.setInt(2, Integer.parseInt(idStr.trim()));
            int rows = ps.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Order status updated to " + selected);
                loadOrdersData();
            } else {
                JOptionPane.showMessageDialog(this, "Order ID not found.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error updating order: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SwingAdminMain app = new SwingAdminMain();
            app.setVisible(true);
        });
    }
}
