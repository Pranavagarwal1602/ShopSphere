package com.shopsphere.service;

import com.shopsphere.model.CartItem;
import com.shopsphere.model.Coupon;
import com.shopsphere.model.Product;
import com.shopsphere.util.PasswordUtil;
import com.shopsphere.util.ValidationUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ShopSphereCoreBusinessTest {

    @Test
    @DisplayName("Test 1: Password hashing produces BCrypt digest and correctly verifies")
    public void testPasswordHashingAndVerification() {
        String plain = "SecretAdmin#2026";
        String hashed = PasswordUtil.hashPassword(plain);

        assertNotNull(hashed);
        assertTrue(hashed.startsWith("$2a$"), "Hash should follow standard BCrypt prefix");
        assertTrue(PasswordUtil.checkPassword(plain, hashed), "Password should match hash");
        assertFalse(PasswordUtil.checkPassword("WrongPassword", hashed), "Incorrect password must be rejected");
    }

    @Test
    @DisplayName("Test 2: ValidationUtil accurately validates email, mobile, and pincodes")
    public void testInputValidation() {
        assertTrue(ValidationUtil.isValidEmail("admin@shopsphere.com"));
        assertTrue(ValidationUtil.isValidEmail("student.name@rtu.ac.in"));
        assertFalse(ValidationUtil.isValidEmail("invalid-email-address"));
        assertFalse(ValidationUtil.isValidEmail("@missingusername.com"));

        assertTrue(ValidationUtil.isValidMobile("9876543210"));
        assertFalse(ValidationUtil.isValidMobile("12345")); // too short

        assertTrue(ValidationUtil.isValidPincode("302017")); // Jaipur
        assertFalse(ValidationUtil.isValidPincode("30201")); // 5 digits
        assertFalse(ValidationUtil.isValidPincode("3020178")); // 7 digits
    }

    @Test
    @DisplayName("Test 3: Product effective price calculation after discount")
    public void testProductEffectivePrice() {
        Product p = new Product();
        p.setPrice(new BigDecimal("4999.00"));
        p.setDiscount(new BigDecimal("500.00"));

        BigDecimal effective = p.getEffectivePrice();
        assertEquals(new BigDecimal("4499.00"), effective);
    }

    @Test
    @DisplayName("Test 4: Cart subtotal and line items total computation")
    public void testCartSubtotalComputation() {
        Product p1 = new Product();
        p1.setPrice(new BigDecimal("1000.00"));
        p1.setDiscount(new BigDecimal("100.00")); // Effective 900.00

        Product p2 = new Product();
        p2.setPrice(new BigDecimal("2500.00"));
        p2.setDiscount(BigDecimal.ZERO); // Effective 2500.00

        CartItem item1 = new CartItem();
        item1.setProduct(p1);
        item1.setQuantity(2); // 900 * 2 = 1800.00

        CartItem item2 = new CartItem();
        item2.setProduct(p2);
        item2.setQuantity(1); // 2500 * 1 = 2500.00

        List<CartItem> cart = new ArrayList<>();
        cart.add(item1);
        cart.add(item2);

        CartService cartService = new CartService(null, null);
        BigDecimal subtotal = cartService.calculateSubtotal(cart);

        assertEquals(new BigDecimal("4300.00"), subtotal);
    }

    @Test
    @DisplayName("Test 5: Coupon model expiry detection")
    public void testCouponExpiry() {
        Coupon expiredCoupon = new Coupon();
        expiredCoupon.setCode("OLD50");
        expiredCoupon.setExpiryDate(Date.valueOf("2020-01-01"));
        assertTrue(expiredCoupon.isExpired());

        Coupon futureCoupon = new Coupon();
        futureCoupon.setCode("FUTURE10");
        futureCoupon.setExpiryDate(Date.valueOf("2030-12-31"));
        assertFalse(futureCoupon.isExpired());
    }
}
