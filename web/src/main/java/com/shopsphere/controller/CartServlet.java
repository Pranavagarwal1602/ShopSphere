package com.shopsphere.controller;

import com.shopsphere.model.CartItem;
import com.shopsphere.model.User;
import com.shopsphere.service.CartService;
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

@WebServlet(name = "CartServlet", urlPatterns = {"/cart", "/cart/add", "/cart/update", "/cart/remove", "/cart/clear"})
public class CartServlet extends HttpServlet {

    private CartService cartService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.cartService = new CartService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = getCurrentUser(req);
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        List<CartItem> items = cartService.getCartItems(user.getUserId());
        BigDecimal subtotal = cartService.calculateSubtotal(items);
        int totalQuantity = items.stream().mapToInt(CartItem::getQuantity).sum();

        req.setAttribute("cartItems", items);
        req.setAttribute("subtotal", subtotal);
        req.setAttribute("totalQuantity", totalQuantity);

        req.getRequestDispatcher("/cart.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = getCurrentUser(req);
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String action = req.getServletPath();
        if (action.endsWith("/add")) {
            handleAdd(req, resp, user.getUserId());
        } else if (action.endsWith("/update")) {
            handleUpdate(req, resp, user.getUserId());
        } else if (action.endsWith("/remove")) {
            handleRemove(req, resp, user.getUserId());
        } else if (action.endsWith("/clear")) {
            cartService.clearCart(user.getUserId());
            resp.sendRedirect(req.getContextPath() + "/cart?msg=" + URLEncoder.encode("Cart cleared.", "UTF-8"));
        } else {
            resp.sendRedirect(req.getContextPath() + "/cart");
        }
    }

    private void handleAdd(HttpServletRequest req, HttpServletResponse resp, int userId) throws IOException {
        try {
            int productId = Integer.parseInt(req.getParameter("productId"));
            int qty = 1;
            if (req.getParameter("quantity") != null) {
                qty = Integer.parseInt(req.getParameter("quantity"));
            }
            String error = cartService.addToCart(userId, productId, qty);
            if (error == null) {
                resp.sendRedirect(req.getContextPath() + "/cart?msg=" + URLEncoder.encode("Item added to cart successfully!", "UTF-8"));
            } else {
                resp.sendRedirect(req.getContextPath() + "/product-details?id=" + productId + "&error=" + URLEncoder.encode(error, "UTF-8"));
            }
        } catch (Exception e) {
            resp.sendRedirect(req.getContextPath() + "/products");
        }
    }

    private void handleUpdate(HttpServletRequest req, HttpServletResponse resp, int userId) throws IOException {
        try {
            int cartId = Integer.parseInt(req.getParameter("cartId"));
            int quantity = Integer.parseInt(req.getParameter("quantity"));
            String error = cartService.updateQuantity(cartId, userId, quantity);
            if (error != null) {
                resp.sendRedirect(req.getContextPath() + "/cart?error=" + URLEncoder.encode(error, "UTF-8"));
            } else {
                resp.sendRedirect(req.getContextPath() + "/cart?msg=" + URLEncoder.encode("Cart updated.", "UTF-8"));
            }
        } catch (Exception e) {
            resp.sendRedirect(req.getContextPath() + "/cart");
        }
    }

    private void handleRemove(HttpServletRequest req, HttpServletResponse resp, int userId) throws IOException {
        try {
            int cartId = Integer.parseInt(req.getParameter("cartId"));
            cartService.removeFromCart(cartId, userId);
            resp.sendRedirect(req.getContextPath() + "/cart?msg=" + URLEncoder.encode("Item removed from cart.", "UTF-8"));
        } catch (Exception e) {
            resp.sendRedirect(req.getContextPath() + "/cart");
        }
    }

    private User getCurrentUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return (session != null) ? (User) session.getAttribute("currentUser") : null;
    }
}
