package com.shopsphere.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

public class Product implements Serializable {
    private static final long serialVersionUID = 1L;

    private int productId;
    private int categoryId;
    private String categoryName; // Helper for joins
    private String name;
    private String brand;
    private String description;
    private BigDecimal price;
    private BigDecimal discount;
    private int stock;
    private String imageUrl;
    private String status; // ACTIVE, INACTIVE
    private Timestamp createdAt;

    // Computed / Auxiliary
    private double averageRating;
    private int reviewCount;

    public Product() {
        this.price = BigDecimal.ZERO;
        this.discount = BigDecimal.ZERO;
        this.stock = 0;
        this.status = "ACTIVE";
    }

    public Product(int productId, int categoryId, String name, String brand, String description,
                   BigDecimal price, BigDecimal discount, int stock, String imageUrl, String status, Timestamp createdAt) {
        this.productId = productId;
        this.categoryId = categoryId;
        this.name = name;
        this.brand = brand;
        this.description = description;
        this.price = price != null ? price : BigDecimal.ZERO;
        this.discount = discount != null ? discount : BigDecimal.ZERO;
        this.stock = stock;
        this.imageUrl = imageUrl;
        this.status = status;
        this.createdAt = createdAt;
    }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public BigDecimal getDiscount() { return discount; }
    public void setDiscount(BigDecimal discount) { this.discount = discount; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public double getAverageRating() { return averageRating; }
    public void setAverageRating(double averageRating) { this.averageRating = averageRating; }

    public int getReviewCount() { return reviewCount; }
    public void setReviewCount(int reviewCount) { this.reviewCount = reviewCount; }

    // Business helper: final selling price after product discount
    public BigDecimal getEffectivePrice() {
        if (discount != null && discount.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal effective = price.subtract(discount);
            return effective.compareTo(BigDecimal.ZERO) > 0 ? effective : BigDecimal.ZERO;
        }
        return price;
    }

    public boolean isInStock() {
        return stock > 0 && "ACTIVE".equalsIgnoreCase(status);
    }
}
