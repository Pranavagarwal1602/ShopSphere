package com.shopsphere.service;

import com.shopsphere.dao.CategoryDAO;
import com.shopsphere.dao.ProductDAO;
import com.shopsphere.model.Category;
import com.shopsphere.model.Product;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class ProductService {
    private final ProductDAO productDAO;
    private final CategoryDAO categoryDAO;

    public ProductService() {
        this.productDAO = new ProductDAO();
        this.categoryDAO = new CategoryDAO();
    }

    public ProductService(ProductDAO productDAO, CategoryDAO categoryDAO) {
        this.productDAO = productDAO;
        this.categoryDAO = categoryDAO;
    }

    public Optional<Product> getProductById(int productId) {
        return productDAO.findById(productId);
    }

    public List<Product> getFeaturedProducts(int limit) {
        return productDAO.findFeatured(limit);
    }

    public List<Product> getDiscountedProducts(int limit) {
        return productDAO.findDiscounted(limit);
    }

    public List<Product> getTopRatedProducts(int limit) {
        return productDAO.findTopRated(limit);
    }

    public List<Product> searchCatalog(String query, Integer categoryId, BigDecimal minPrice, BigDecimal maxPrice,
                                       String sort, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return productDAO.searchAndFilter(query, categoryId, minPrice, maxPrice, sort, offset, pageSize);
    }

    public int countCatalog(String query, Integer categoryId, BigDecimal minPrice, BigDecimal maxPrice) {
        return productDAO.countSearchAndFilter(query, categoryId, minPrice, maxPrice);
    }

    public List<Category> getActiveCategories() {
        return categoryDAO.findAllActive();
    }

    public List<Category> getAllCategories() {
        return categoryDAO.findAll();
    }

    public Optional<Category> getCategoryById(int categoryId) {
        return categoryDAO.findById(categoryId);
    }

    public boolean saveCategory(Category category) {
        if (category.getName() == null || category.getName().trim().isEmpty()) return false;
        if (category.getCategoryId() > 0) {
            return categoryDAO.update(category);
        } else {
            return categoryDAO.create(category);
        }
    }

    public boolean deleteCategory(int categoryId) {
        return categoryDAO.delete(categoryId);
    }

    public List<Product> listProductsForAdmin(String keyword, Integer categoryId) {
        return productDAO.findAllForAdmin(keyword, categoryId);
    }

    public List<Product> getLowStockProducts(int threshold) {
        return productDAO.findLowStock(threshold);
    }

    public boolean saveProduct(Product product) {
        if (product.getName() == null || product.getName().trim().isEmpty()) return false;
        if (product.getPrice() == null || product.getPrice().compareTo(BigDecimal.ZERO) < 0) return false;
        if (product.getStock() < 0) return false;

        if (product.getProductId() > 0) {
            return productDAO.update(product);
        } else {
            return productDAO.create(product);
        }
    }

    public boolean updateStock(int productId, int newStock) {
        if (newStock < 0) return false;
        return productDAO.updateStock(productId, newStock);
    }

    public int countTotalProducts() {
        return productDAO.countTotalProducts();
    }
}
