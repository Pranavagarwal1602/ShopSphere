package com.shopsphere.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;

public class Coupon implements Serializable {
    private static final long serialVersionUID = 1L;

    private int couponId;
    private String code;
    private String discountType; // PERCENTAGE, FIXED
    private BigDecimal discountValue;
    private BigDecimal minimumOrder;
    private BigDecimal maximumDiscount;
    private Date expiryDate;
    private String status; // ACTIVE, INACTIVE

    public Coupon() {
        this.discountValue = BigDecimal.ZERO;
        this.minimumOrder = BigDecimal.ZERO;
        this.maximumDiscount = BigDecimal.ZERO;
        this.status = "ACTIVE";
    }

    public Coupon(int couponId, String code, String discountType, BigDecimal discountValue,
                  BigDecimal minimumOrder, BigDecimal maximumDiscount, Date expiryDate, String status) {
        this.couponId = couponId;
        this.code = code;
        this.discountType = discountType;
        this.discountValue = discountValue;
        this.minimumOrder = minimumOrder;
        this.maximumDiscount = maximumDiscount;
        this.expiryDate = expiryDate;
        this.status = status;
    }

    public int getCouponId() { return couponId; }
    public void setCouponId(int couponId) { this.couponId = couponId; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getDiscountType() { return discountType; }
    public void setDiscountType(String discountType) { this.discountType = discountType; }

    public BigDecimal getDiscountValue() { return discountValue; }
    public void setDiscountValue(BigDecimal discountValue) { this.discountValue = discountValue; }

    public BigDecimal getMinimumOrder() { return minimumOrder; }
    public void setMinimumOrder(BigDecimal minimumOrder) { this.minimumOrder = minimumOrder; }

    public BigDecimal getMaximumDiscount() { return maximumDiscount; }
    public void setMaximumDiscount(BigDecimal maximumDiscount) { this.maximumDiscount = maximumDiscount; }

    public Date getExpiryDate() { return expiryDate; }
    public void setExpiryDate(Date expiryDate) { this.expiryDate = expiryDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public boolean isExpired() {
        if (expiryDate == null) return false;
        long today = System.currentTimeMillis();
        return expiryDate.getTime() < today;
    }

    public boolean isActive() {
        return "ACTIVE".equalsIgnoreCase(status) && !isExpired();
    }
}
