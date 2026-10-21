package com.shopsphere.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public class InventoryDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private int productId;
    private String name;
    private String brand;
    private String categoryName;
    private BigDecimal price;
    private int stock;
    private String status;

    public InventoryDTO() {}

    public InventoryDTO(int productId, String name, String brand, String categoryName, BigDecimal price, int stock, String status) {
        this.productId = productId;
        this.name = name;
        this.brand = brand;
        this.categoryName = categoryName;
        this.price = price;
        this.stock = stock;
        this.status = status;
    }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return String.format("[%d] %s (%s) - Stock: %d | Price: ₹%.2f | Status: %s",
                productId, name, brand, stock, price, status);
    }
}
