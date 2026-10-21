package com.shopsphere.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Order implements Serializable {
    private static final long serialVersionUID = 1L;

    private int orderId;
    private int userId;
    private Integer addressId;
    private BigDecimal totalAmount;
    private BigDecimal discount;
    private String paymentMethod; // COD, ONLINE_MOCK
    private String paymentStatus; // PENDING, COMPLETED, FAILED, REFUNDED
    private String orderStatus; // PLACED, CONFIRMED, SHIPPED, DELIVERED, CANCELLED
    private Timestamp createdAt;

    // Helper references for display
    private String customerName;
    private String customerEmail;
    private Address address;
    private List<OrderItem> items = new ArrayList<>();

    public Order() {
        this.totalAmount = BigDecimal.ZERO;
        this.discount = BigDecimal.ZERO;
        this.paymentStatus = "PENDING";
        this.orderStatus = "PLACED";
    }

    public Order(int orderId, int userId, Integer addressId, BigDecimal totalAmount, BigDecimal discount,
                 String paymentMethod, String paymentStatus, String orderStatus, Timestamp createdAt) {
        this.orderId = orderId;
        this.userId = userId;
        this.addressId = addressId;
        this.totalAmount = totalAmount;
        this.discount = discount;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.orderStatus = orderStatus;
        this.createdAt = createdAt;
    }

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public Integer getAddressId() { return addressId; }
    public void setAddressId(Integer addressId) { this.addressId = addressId; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public BigDecimal getDiscount() { return discount; }
    public void setDiscount(BigDecimal discount) { this.discount = discount; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getOrderStatus() { return orderStatus; }
    public void setOrderStatus(String orderStatus) { this.orderStatus = orderStatus; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public Address getAddress() { return address; }
    public void setAddress(Address address) { this.address = address; }

    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }

    public boolean isCancellable() {
        return "PLACED".equalsIgnoreCase(orderStatus) || "CONFIRMED".equalsIgnoreCase(orderStatus);
    }
}
