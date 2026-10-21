package com.shopsphere.service;

import com.shopsphere.dao.*;
import com.shopsphere.model.*;
import com.shopsphere.util.DBConnectionUtil;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service managing the complete atomic checkout process.
 * Wraps order creation, order-item creation, inventory updates, coupon recording,
 * mock payment transaction, and cart clearing inside a single ACID JDBC Transaction.
 */
public class CheckoutService {
    private static final Logger LOGGER = Logger.getLogger(CheckoutService.class.getName());

    private final CartDAO cartDAO;
    private final ProductDAO productDAO;
    private final OrderDAO orderDAO;
    private final PaymentDAO paymentDAO;
    private final CouponDAO couponDAO;
    private final CouponService couponService;

    public CheckoutService() {
        this.cartDAO = new CartDAO();
        this.productDAO = new ProductDAO();
        this.orderDAO = new OrderDAO();
        this.paymentDAO = new PaymentDAO();
        this.couponDAO = new CouponDAO();
        this.couponService = new CouponService(couponDAO);
    }

    public static class CheckoutResult {
        private final boolean success;
        private final String message;
        private final Integer orderId;

        public CheckoutResult(boolean success, String message, Integer orderId) {
            this.success = success;
            this.message = message;
            this.orderId = orderId;
        }

        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public Integer getOrderId() { return orderId; }
    }

    public CheckoutResult processOrder(int userId, int addressId, String paymentMethod, String couponCode) {
        if (addressId <= 0) {
            return new CheckoutResult(false, "Please select a valid delivery address.", null);
        }
        if (paymentMethod == null || (!paymentMethod.equalsIgnoreCase("COD") && !paymentMethod.equalsIgnoreCase("ONLINE_MOCK"))) {
            return new CheckoutResult(false, "Please select a valid payment method (Cash on Delivery or Mock Online Payment).", null);
        }

        List<CartItem> cartItems = cartDAO.findByUserId(userId);
        if (cartItems == null || cartItems.isEmpty()) {
            return new CheckoutResult(false, "Your shopping cart is empty.", null);
        }

        // 1. Re-validate stock for every item before opening transaction
        for (CartItem item : cartItems) {
            if (item.getProduct() == null || item.getQuantity() > item.getProduct().getStock()) {
                String name = item.getProduct() != null ? item.getProduct().getName() : "Product";
                return new CheckoutResult(false, "Insufficient stock for: " + name + ". Please update your cart.", null);
            }
        }

        // 2. Server-side subtotal calculation
        BigDecimal subtotal = BigDecimal.ZERO;
        for (CartItem item : cartItems) {
            subtotal = subtotal.add(item.getItemTotal());
        }

        // 3. Coupon validation & discount calculation
        BigDecimal discount = BigDecimal.ZERO;
        Coupon appliedCoupon = null;
        if (couponCode != null && !couponCode.trim().isEmpty()) {
            CouponService.CouponValidationResult couponResult = couponService.validateAndCalculate(couponCode, userId, subtotal);
            if (couponResult.isValid()) {
                discount = couponResult.getDiscountAmount();
                appliedCoupon = couponResult.getCoupon();
            } else {
                return new CheckoutResult(false, "Coupon Error: " + couponResult.getMessage(), null);
            }
        }

        BigDecimal finalTotal = subtotal.subtract(discount);
        if (finalTotal.compareTo(BigDecimal.ZERO) < 0) {
            finalTotal = BigDecimal.ZERO;
        }

        // 4. Begin Multi-Table ACID Transaction
        Connection conn = null;
        try {
            conn = DBConnectionUtil.getConnection();
            conn.setAutoCommit(false); // START TRANSACTION

            // Create Order
            Order order = new Order();
            order.setUserId(userId);
            order.setAddressId(addressId);
            order.setTotalAmount(finalTotal);
            order.setDiscount(discount);
            order.setPaymentMethod(paymentMethod);
            order.setPaymentStatus("ONLINE_MOCK".equalsIgnoreCase(paymentMethod) ? "COMPLETED" : "PENDING");
            order.setOrderStatus("PLACED");

            int orderId = orderDAO.insertOrder(conn, order);

            // Insert Order Items and safely decrement inventory
            for (CartItem item : cartItems) {
                OrderItem oi = new OrderItem();
                oi.setOrderId(orderId);
                oi.setProductId(item.getProductId());
                oi.setQuantity(item.getQuantity());
                oi.setPrice(item.getProduct().getEffectivePrice());
                orderDAO.insertOrderItem(conn, oi);

                // Atomic stock decrement
                boolean stockUpdated = productDAO.updateStockDelta(conn, item.getProductId(), -item.getQuantity());
                if (!stockUpdated) {
                    conn.rollback();
                    return new CheckoutResult(false, "Stock conflict occurred for " + item.getProduct().getName() + ". Order cancelled.", null);
                }
            }

            // Record Payment
            Payment payment = new Payment();
            payment.setOrderId(orderId);
            payment.setPaymentMethod(paymentMethod);
            payment.setAmount(finalTotal);
            payment.setPaymentStatus("ONLINE_MOCK".equalsIgnoreCase(paymentMethod) ? "COMPLETED" : "PENDING");
            payment.setTransactionReference("TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            paymentDAO.insertPayment(conn, payment);

            // Record Coupon Usage if coupon was applied
            if (appliedCoupon != null) {
                couponDAO.recordUsage(conn, appliedCoupon.getCouponId(), userId, orderId);
            }

            // Clear User Cart
            cartDAO.clearCart(conn, userId);

            // Commit Transaction
            conn.commit();
            LOGGER.info("Successfully placed Order ID: " + orderId + " for User ID: " + userId);
            return new CheckoutResult(true, "Your order #" + orderId + " has been placed successfully!", orderId);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Transaction failure during checkout for userId: " + userId, e);
            if (conn != null) {
                try {
                    conn.rollback();
                    LOGGER.warning("Transaction rolled back for checkout.");
                } catch (SQLException ex) {
                    LOGGER.log(Level.SEVERE, "Rollback failed", ex);
                }
            }
            return new CheckoutResult(false, "Order placement failed due to a system error. No funds or stock were deducted.", null);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    LOGGER.log(Level.WARNING, "Error closing connection: " + e.getMessage());
                }
            }
        }
    }
}
