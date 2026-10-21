package com.shopsphere.service;

import com.shopsphere.dao.CartDAO;
import com.shopsphere.dao.ProductDAO;
import com.shopsphere.model.CartItem;
import com.shopsphere.model.Product;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class CartService {
    private final CartDAO cartDAO;
    private final ProductDAO productDAO;

    public CartService() {
        this.cartDAO = new CartDAO();
        this.productDAO = new ProductDAO();
    }

    public CartService(CartDAO cartDAO, ProductDAO productDAO) {
        this.cartDAO = cartDAO;
        this.productDAO = productDAO;
    }

    public List<CartItem> getCartItems(int userId) {
        return cartDAO.findByUserId(userId);
    }

    public String addToCart(int userId, int productId, int requestedQty) {
        if (requestedQty <= 0) requestedQty = 1;

        Optional<Product> prodOpt = productDAO.findById(productId);
        if (prodOpt.isEmpty() || !"ACTIVE".equalsIgnoreCase(prodOpt.get().getStatus())) {
            return "This product is currently unavailable.";
        }

        Product product = prodOpt.get();
        if (product.getStock() <= 0) {
            return "Sorry, this product is currently out of stock.";
        }

        // Check if user already has this product in cart
        Optional<CartItem> existingOpt = cartDAO.findByUserAndProduct(userId, productId);
        int currentInCart = existingOpt.map(CartItem::getQuantity).orElse(0);
        int totalDesired = currentInCart + requestedQty;

        if (totalDesired > product.getStock()) {
            return "Cannot add more. Available stock is " + product.getStock() + " (you already have " + currentInCart + " in cart).";
        }

        boolean success = cartDAO.addItem(userId, productId, requestedQty);
        return success ? null : "Failed to update cart. Please try again.";
    }

    public String updateQuantity(int cartId, int userId, int newQuantity) {
        if (newQuantity <= 0) {
            cartDAO.removeItem(cartId, userId);
            return null;
        }

        List<CartItem> items = cartDAO.findByUserId(userId);
        CartItem target = items.stream().filter(i -> i.getCartId() == cartId).findFirst().orElse(null);
        if (target == null) {
            return "Cart item not found.";
        }

        Optional<Product> prodOpt = productDAO.findById(target.getProductId());
        if (prodOpt.isPresent() && newQuantity > prodOpt.get().getStock()) {
            return "Cannot set quantity to " + newQuantity + ". Maximum available stock is " + prodOpt.get().getStock() + ".";
        }

        boolean success = cartDAO.updateQuantity(cartId, userId, newQuantity);
        return success ? null : "Failed to update quantity.";
    }

    public boolean removeFromCart(int cartId, int userId) {
        return cartDAO.removeItem(cartId, userId);
    }

    public boolean clearCart(int userId) {
        return cartDAO.clearCart(userId);
    }

    public int getCartCount(int userId) {
        return cartDAO.countCartItems(userId);
    }

    public BigDecimal calculateSubtotal(List<CartItem> items) {
        BigDecimal sum = BigDecimal.ZERO;
        for (CartItem item : items) {
            sum = sum.add(item.getItemTotal());
        }
        return sum;
    }
}
