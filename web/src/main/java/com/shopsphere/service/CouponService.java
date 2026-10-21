package com.shopsphere.service;

import com.shopsphere.dao.CouponDAO;
import com.shopsphere.model.Coupon;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

public class CouponService {
    private final CouponDAO couponDAO;

    public CouponService() {
        this.couponDAO = new CouponDAO();
    }

    public CouponService(CouponDAO couponDAO) {
        this.couponDAO = couponDAO;
    }

    public static class CouponValidationResult {
        private final boolean valid;
        private final String message;
        private final Coupon coupon;
        private final BigDecimal discountAmount;

        public CouponValidationResult(boolean valid, String message, Coupon coupon, BigDecimal discountAmount) {
            this.valid = valid;
            this.message = message;
            this.coupon = coupon;
            this.discountAmount = discountAmount != null ? discountAmount : BigDecimal.ZERO;
        }

        public boolean isValid() { return valid; }
        public String getMessage() { return message; }
        public Coupon getCoupon() { return coupon; }
        public BigDecimal getDiscountAmount() { return discountAmount; }
    }

    public CouponValidationResult validateAndCalculate(String couponCode, int userId, BigDecimal cartSubtotal) {
        if (couponCode == null || couponCode.trim().isEmpty()) {
            return new CouponValidationResult(false, "Please enter a coupon code.", null, BigDecimal.ZERO);
        }

        Optional<Coupon> opt = couponDAO.findByCode(couponCode.trim());
        if (opt.isEmpty()) {
            return new CouponValidationResult(false, "Invalid coupon code.", null, BigDecimal.ZERO);
        }

        Coupon coupon = opt.get();

        if (!"ACTIVE".equalsIgnoreCase(coupon.getStatus())) {
            return new CouponValidationResult(false, "This coupon is no longer active.", coupon, BigDecimal.ZERO);
        }

        if (coupon.isExpired()) {
            return new CouponValidationResult(false, "This coupon has expired.", coupon, BigDecimal.ZERO);
        }

        if (coupon.getMinimumOrder() != null && cartSubtotal.compareTo(coupon.getMinimumOrder()) < 0) {
            return new CouponValidationResult(false,
                    String.format("Minimum order value for this coupon is ₹%.2f (Current: ₹%.2f)",
                            coupon.getMinimumOrder(), cartSubtotal),
                    coupon, BigDecimal.ZERO);
        }

        if (couponDAO.hasUserUsedCoupon(coupon.getCouponId(), userId)) {
            return new CouponValidationResult(false, "You have already used this coupon code.", coupon, BigDecimal.ZERO);
        }

        // Calculate discount
        BigDecimal discount = BigDecimal.ZERO;
        if ("PERCENTAGE".equalsIgnoreCase(coupon.getDiscountType())) {
            discount = cartSubtotal.multiply(coupon.getDiscountValue()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            if (coupon.getMaximumDiscount() != null && coupon.getMaximumDiscount().compareTo(BigDecimal.ZERO) > 0) {
                if (discount.compareTo(coupon.getMaximumDiscount()) > 0) {
                    discount = coupon.getMaximumDiscount();
                }
            }
        } else if ("FIXED".equalsIgnoreCase(coupon.getDiscountType())) {
            discount = coupon.getDiscountValue();
            if (discount.compareTo(cartSubtotal) > 0) {
                discount = cartSubtotal; // Cannot discount more than order amount
            }
        }

        return new CouponValidationResult(true, "Coupon applied successfully!", coupon, discount);
    }

    public List<Coupon> getCustomerAvailableCoupons() {
        return couponDAO.findAvailableForCustomers();
    }

    public List<Coupon> getAllCoupons() {
        return couponDAO.findAll();
    }

    public boolean saveCoupon(Coupon coupon) {
        if (coupon.getCode() == null || coupon.getCode().trim().isEmpty()) return false;
        if (coupon.getDiscountValue() == null || coupon.getDiscountValue().compareTo(BigDecimal.ZERO) <= 0) return false;
        if (coupon.getCouponId() > 0) {
            return couponDAO.update(coupon);
        } else {
            return couponDAO.create(coupon);
        }
    }

    public boolean deleteCoupon(int couponId) {
        return couponDAO.delete(couponId);
    }
}
