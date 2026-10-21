package com.shopsphere.controller;

import com.shopsphere.model.User;
import com.shopsphere.service.ReviewService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;

@WebServlet(name = "ReviewServlet", urlPatterns = {"/review/submit"})
public class ReviewServlet extends HttpServlet {

    private ReviewService reviewService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.reviewService = new ReviewService();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("currentUser");
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        try {
            int productId = Integer.parseInt(req.getParameter("productId"));
            int rating = Integer.parseInt(req.getParameter("rating"));
            String comment = req.getParameter("reviewText");

            String error = reviewService.submitReview(user.getUserId(), productId, rating, comment);
            if (error == null) {
                resp.sendRedirect(req.getContextPath() + "/product-details?id=" + productId + "&msg=" + URLEncoder.encode("Thank you for your review!", "UTF-8"));
            } else {
                resp.sendRedirect(req.getContextPath() + "/product-details?id=" + productId + "&error=" + URLEncoder.encode(error, "UTF-8"));
            }
        } catch (Exception e) {
            resp.sendRedirect(req.getContextPath() + "/products");
        }
    }
}
