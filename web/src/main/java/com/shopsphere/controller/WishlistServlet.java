package com.shopsphere.controller;

import com.shopsphere.model.User;
import com.shopsphere.model.WishlistItem;
import com.shopsphere.service.WishlistService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

@WebServlet(name = "WishlistServlet", urlPatterns = {"/wishlist", "/wishlist/add", "/wishlist/remove", "/wishlist/move-to-cart"})
public class WishlistServlet extends HttpServlet {

    private WishlistService wishlistService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.wishlistService = new WishlistService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("currentUser");
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        List<WishlistItem> items = wishlistService.getWishlist(user.getUserId());
        req.setAttribute("wishlistItems", items);
        req.getRequestDispatcher("/wishlist.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("currentUser");
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String path = req.getServletPath();
        try {
            if ("/wishlist/add".equals(path)) {
                int productId = Integer.parseInt(req.getParameter("productId"));
                wishlistService.addToWishlist(user.getUserId(), productId);
                resp.sendRedirect(req.getContextPath() + "/wishlist?msg=" + URLEncoder.encode("Product added to your wishlist!", "UTF-8"));
            } else if ("/wishlist/remove".equals(path)) {
                int wishlistId = Integer.parseInt(req.getParameter("wishlistId"));
                wishlistService.removeFromWishlist(wishlistId, user.getUserId());
                resp.sendRedirect(req.getContextPath() + "/wishlist?msg=" + URLEncoder.encode("Item removed from wishlist.", "UTF-8"));
            } else if ("/wishlist/move-to-cart".equals(path)) {
                int wishlistId = Integer.parseInt(req.getParameter("wishlistId"));
                int productId = Integer.parseInt(req.getParameter("productId"));
                boolean moved = wishlistService.moveToCart(wishlistId, user.getUserId(), productId);
                if (moved) {
                    resp.sendRedirect(req.getContextPath() + "/cart?msg=" + URLEncoder.encode("Item moved to your cart!", "UTF-8"));
                } else {
                    resp.sendRedirect(req.getContextPath() + "/wishlist?error=" + URLEncoder.encode("Failed to move item to cart.", "UTF-8"));
                }
            }
        } catch (Exception e) {
            resp.sendRedirect(req.getContextPath() + "/wishlist");
        }
    }
}
