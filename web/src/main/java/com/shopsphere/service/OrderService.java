package com.shopsphere.service;

import com.shopsphere.dao.OrderDAO;
import com.shopsphere.dao.ProductDAO;
import com.shopsphere.model.Order;
import com.shopsphere.model.OrderItem;
import com.shopsphere.util.DBConnectionUtil;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OrderService {
    private static final Logger LOGGER = Logger.getLogger(OrderService.class.getName());

    private final OrderDAO orderDAO;
    private final ProductDAO productDAO;

    public OrderService() {
        this.orderDAO = new OrderDAO();
        this.productDAO = new ProductDAO();
    }

    public OrderService(OrderDAO orderDAO, ProductDAO productDAO) {
        this.orderDAO = orderDAO;
        this.productDAO = productDAO;
    }

    public Optional<Order> getOrderForUser(int orderId, int userId) {
        Optional<Order> orderOpt = orderDAO.findById(orderId);
        if (orderOpt.isPresent() && orderOpt.get().getUserId() == userId) {
            return orderOpt;
        }
        return Optional.empty();
    }

    public Optional<Order> getOrderForAdmin(int orderId) {
        return orderDAO.findById(orderId);
    }

    public List<Order> getOrdersByUser(int userId) {
        return orderDAO.findByUserId(userId);
    }

    public List<Order> listAllOrders(String statusFilter) {
        return orderDAO.findAllForAdmin(statusFilter);
    }

    public boolean updateOrderStatus(int orderId, String newStatus) {
        return orderDAO.updateOrderStatus(orderId, newStatus);
    }

    public boolean updatePaymentStatus(int orderId, String paymentStatus) {
        return orderDAO.updatePaymentStatus(orderId, paymentStatus);
    }

    public boolean cancelOrder(int orderId, int userId) {
        Optional<Order> orderOpt = getOrderForUser(orderId, userId);
        if (orderOpt.isEmpty()) return false;

        Order order = orderOpt.get();
        if (!order.isCancellable()) {
            return false;
        }

        // Restoring stock inside transaction
        Connection conn = null;
        try {
            conn = DBConnectionUtil.getConnection();
            conn.setAutoCommit(false);

            // Update status to CANCELLED
            boolean updated = orderDAO.updateOrderStatus(orderId, "CANCELLED");
            if (!updated) {
                conn.rollback();
                return false;
            }

            // Restore items stock
            List<OrderItem> items = orderDAO.findItemsByOrderId(orderId);
            for (OrderItem oi : items) {
                productDAO.updateStockDelta(conn, oi.getProductId(), oi.getQuantity());
            }

            conn.commit();
            return true;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to cancel order: " + orderId, e);
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ignored) {}
            }
        }
    }

    public BigDecimal getTotalSalesRevenue() {
        return orderDAO.getTotalSalesRevenue();
    }

    public int countTotalOrders() {
        return orderDAO.countTotalOrders();
    }

    public int countPendingOrders() {
        return orderDAO.countPendingOrders();
    }
}
