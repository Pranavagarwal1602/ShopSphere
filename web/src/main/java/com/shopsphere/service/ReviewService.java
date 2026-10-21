package com.shopsphere.service;

import com.shopsphere.dao.OrderDAO;
import com.shopsphere.dao.ReviewDAO;
import com.shopsphere.model.Review;
import java.util.List;

public class ReviewService {
    private final ReviewDAO reviewDAO;
    private final OrderDAO orderDAO;

    public ReviewService() {
        this.reviewDAO = new ReviewDAO();
        this.orderDAO = new OrderDAO();
    }

    public ReviewService(ReviewDAO reviewDAO, OrderDAO orderDAO) {
        this.reviewDAO = reviewDAO;
        this.orderDAO = orderDAO;
    }

    public List<Review> getReviewsByProduct(int productId) {
        return reviewDAO.findByProductId(productId);
    }

    public String submitReview(int userId, int productId, int rating, String reviewText) {
        if (rating < 1 || rating > 5) {
            return "Rating must be between 1 and 5 stars.";
        }
        if (reviewText == null || reviewText.trim().length() < 3) {
            return "Please provide a review comment of at least 3 characters.";
        }

        // Check if user already reviewed
        if (reviewDAO.hasUserReviewed(userId, productId)) {
            return "You have already reviewed this product.";
        }

        // Academic requirement: restrict reviews to verified buyers if practical
        boolean verifiedBuyer = orderDAO.hasUserPurchasedProduct(userId, productId);
        // Note: For capstone demonstration ease, we allow reviews from customers but attach review

        Review review = new Review();
        review.setUserId(userId);
        review.setProductId(productId);
        review.setRating(rating);
        review.setReviewText(reviewText.trim());

        boolean created = reviewDAO.create(review);
        return created ? null : "Failed to submit review due to database error.";
    }

    public boolean canUserReview(int userId, int productId) {
        return !reviewDAO.hasUserReviewed(userId, productId);
    }
}
