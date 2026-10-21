package com.shopsphere.controller;

import com.shopsphere.model.Product;
import com.shopsphere.model.Review;
import com.shopsphere.model.User;
import com.shopsphere.service.ProductService;
import com.shopsphere.service.ReviewService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Controller for displaying product details and recently viewed products.
 * Demonstrates: Cookies (recently viewed IDs), Session tracking, and Product Reviews.
 */
@WebServlet(name = "ProductDetailServlet", urlPatterns = {"/product-details"})
public class ProductDetailServlet extends HttpServlet {

    private ProductService productService;
    private ReviewService reviewService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.productService = new ProductService();
        this.reviewService = new ReviewService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idParam = req.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/products");
            return;
        }

        int productId;
        try {
            productId = Integer.parseInt(idParam.trim());
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/products");
            return;
        }

        Optional<Product> prodOpt = productService.getProductById(productId);
        if (prodOpt.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/products?error=ProductNotFound");
            return;
        }

        Product product = prodOpt.get();
        List<Review> reviews = reviewService.getReviewsByProduct(productId);

        // Capability to submit review check
        HttpSession session = req.getSession(false);
        boolean canReview = false;
        if (session != null && session.getAttribute("currentUser") != null) {
            User user = (User) session.getAttribute("currentUser");
            canReview = reviewService.canUserReview(user.getUserId(), productId);
        }

        // Syllabus demonstration: Track Recently Viewed Products via non-sensitive HTTP Cookies
        trackRecentlyViewedCookie(req, resp, productId);

        // Fetch up to 3 recently viewed products from Cookie
        List<Product> recentlyViewed = getRecentlyViewedFromCookies(req, productId);

        req.setAttribute("product", product);
        req.setAttribute("reviews", reviews);
        req.setAttribute("canReview", canReview);
        req.setAttribute("recentlyViewed", recentlyViewed);

        req.getRequestDispatcher("/product-details.jsp").forward(req, resp);
    }

    private void trackRecentlyViewedCookie(HttpServletRequest req, HttpServletResponse resp, int currentProductId) {
        String cookieVal = "";
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if ("recent_products".equals(c.getName())) {
                    cookieVal = c.getValue();
                    break;
                }
            }
        }

        List<String> ids = new ArrayList<>(Arrays.asList(cookieVal.split("-")));
        ids.removeIf(id -> id.isEmpty() || id.equals(String.valueOf(currentProductId)));
        ids.add(0, String.valueOf(currentProductId));
        if (ids.size() > 5) {
            ids = ids.subList(0, 5);
        }

        Cookie updatedCookie = new Cookie("recent_products", String.join("-", ids));
        updatedCookie.setMaxAge(7 * 24 * 60 * 60); // 7 days
        updatedCookie.setPath(req.getContextPath().isEmpty() ? "/" : req.getContextPath());
        updatedCookie.setHttpOnly(true);
        resp.addCookie(updatedCookie);
    }

    private List<Product> getRecentlyViewedFromCookies(HttpServletRequest req, int currentProductId) {
        List<Product> list = new ArrayList<>();
        Cookie[] cookies = req.getCookies();
        if (cookies == null) return list;

        for (Cookie c : cookies) {
            if ("recent_products".equals(c.getName())) {
                String[] ids = c.getValue().split("-");
                for (String s : ids) {
                    try {
                        int id = Integer.parseInt(s.trim());
                        if (id != currentProductId) {
                            productService.getProductById(id).ifPresent(list::add);
                            if (list.size() >= 3) break;
                        }
                    } catch (NumberFormatException ignored) {}
                }
                break;
            }
        }
        return list;
    }
}
