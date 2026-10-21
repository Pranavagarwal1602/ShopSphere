package com.shopsphere.controller;

import com.shopsphere.model.Address;
import com.shopsphere.model.CartItem;
import com.shopsphere.model.Coupon;
import com.shopsphere.model.User;
import com.shopsphere.service.AddressService;
import com.shopsphere.service.CartService;
import com.shopsphere.service.CheckoutService;
import com.shopsphere.service.CouponService;
import com.shopsphere.util.ValidationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.List;

@WebServlet(name = "CheckoutServlet", urlPatterns = {"/checkout", "/checkout/place-order"})
public class CheckoutServlet extends HttpServlet {

    private CartService cartService;
    private AddressService addressService;
    private CouponService couponService;
    private CheckoutService checkoutService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.cartService = new CartService();
        this.addressService = new AddressService();
        this.couponService = new CouponService();
        this.checkoutService = new CheckoutService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("currentUser");
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        List<CartItem> cartItems = cartService.getCartItems(user.getUserId());
        if (cartItems.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/cart?error=" + URLEncoder.encode("Your cart is empty.", "UTF-8"));
            return;
        }

        BigDecimal subtotal = cartService.calculateSubtotal(cartItems);
        List<Address> addresses = addressService.getAddresses(user.getUserId());
        List<Coupon> availableCoupons = couponService.getCustomerAvailableCoupons();

        // Optional applied coupon from request/session
        String appliedCode = req.getParameter("coupon");
        BigDecimal discount = BigDecimal.ZERO;
        String couponMsg = null;
        if (appliedCode != null && !appliedCode.trim().isEmpty()) {
            CouponService.CouponValidationResult couponResult =
                    couponService.validateAndCalculate(appliedCode.trim(), user.getUserId(), subtotal);
            if (couponResult.isValid()) {
                discount = couponResult.getDiscountAmount();
                couponMsg = "Coupon " + appliedCode.toUpperCase() + " applied: -₹" + discount;
            } else {
                req.setAttribute("couponError", couponResult.getMessage());
                appliedCode = null;
            }
        }

        BigDecimal finalTotal = subtotal.subtract(discount);
        if (finalTotal.compareTo(BigDecimal.ZERO) < 0) finalTotal = BigDecimal.ZERO;

        // Generate CSRF token for checkout form
        String csrfToken = ValidationUtil.generateToken();
        req.getSession().setAttribute("checkoutCsrf", csrfToken);

        req.setAttribute("cartItems", cartItems);
        req.setAttribute("subtotal", subtotal);
        req.setAttribute("addresses", addresses);
        req.setAttribute("availableCoupons", availableCoupons);
        req.setAttribute("appliedCouponCode", appliedCode);
        req.setAttribute("discount", discount);
        req.setAttribute("couponMsg", couponMsg);
        req.setAttribute("finalTotal", finalTotal);
        req.setAttribute("csrfToken", csrfToken);

        req.getRequestDispatcher("/checkout.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("currentUser");
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String formCsrf = req.getParameter("csrfToken");
        String sessionCsrf = (String) req.getSession().getAttribute("checkoutCsrf");
        if (sessionCsrf == null || !sessionCsrf.equals(formCsrf)) {
            resp.sendRedirect(req.getContextPath() + "/checkout?error=" + URLEncoder.encode("Session timed out or invalid submission.", "UTF-8"));
            return;
        }

        // Consume CSRF token to prevent accidental double-submit
        req.getSession().removeAttribute("checkoutCsrf");

        int addressId;
        try {
            addressId = Integer.parseInt(req.getParameter("addressId"));
        } catch (Exception e) {
            resp.sendRedirect(req.getContextPath() + "/checkout?error=" + URLEncoder.encode("Please select a delivery address.", "UTF-8"));
            return;
        }

        String paymentMethod = req.getParameter("paymentMethod");
        String couponCode = req.getParameter("couponCode");

        CheckoutService.CheckoutResult result = checkoutService.processOrder(user.getUserId(), addressId, paymentMethod, couponCode);

        if (result.isSuccess()) {
            resp.sendRedirect(req.getContextPath() + "/order-success?orderId=" + result.getOrderId());
        } else {
            resp.sendRedirect(req.getContextPath() + "/checkout?error=" + URLEncoder.encode(result.getMessage(), "UTF-8"));
        }
    }
}
