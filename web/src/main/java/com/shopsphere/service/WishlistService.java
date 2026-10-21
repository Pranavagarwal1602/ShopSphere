package com.shopsphere.service;

import com.shopsphere.dao.CartDAO;
import com.shopsphere.dao.WishlistDAO;
import com.shopsphere.model.WishlistItem;
import java.util.List;

public class WishlistService {
    private final WishlistDAO wishlistDAO;
    private final CartDAO cartDAO;

    public WishlistService() {
        this.wishlistDAO = new WishlistDAO();
        this.cartDAO = new CartDAO();
    }

    public WishlistService(WishlistDAO wishlistDAO, CartDAO cartDAO) {
        this.wishlistDAO = wishlistDAO;
        this.cartDAO = cartDAO;
    }

    public List<WishlistItem> getWishlist(int userId) {
        return wishlistDAO.findByUserId(userId);
    }

    public boolean addToWishlist(int userId, int productId) {
        return wishlistDAO.add(userId, productId);
    }

    public boolean removeFromWishlist(int wishlistId, int userId) {
        return wishlistDAO.remove(wishlistId, userId);
    }

    public boolean moveToCart(int wishlistId, int userId, int productId) {
        boolean added = cartDAO.addItem(userId, productId, 1);
        if (added) {
            wishlistDAO.remove(wishlistId, userId);
            return true;
        }
        return false;
    }

    public boolean isInWishlist(int userId, int productId) {
        return wishlistDAO.exists(userId, productId);
    }
}
