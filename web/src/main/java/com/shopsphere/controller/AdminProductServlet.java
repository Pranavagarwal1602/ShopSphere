package com.shopsphere.controller;

import com.shopsphere.model.Category;
import com.shopsphere.model.Product;
import com.shopsphere.service.ProductService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.List;

@WebServlet(name = "AdminProductServlet", urlPatterns = {"/admin/products", "/admin/products/save", "/admin/products/delete", "/admin/products/toggle-status"})
public class AdminProductServlet extends HttpServlet {

    private ProductService productService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.productService = new ProductService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String keyword = req.getParameter("q");
        String catParam = req.getParameter("category");
        Integer categoryId = null;
        if (catParam != null && !catParam.trim().isEmpty()) {
            try { categoryId = Integer.parseInt(catParam.trim()); } catch (Exception ignored) {}
        }

        List<Product> products = productService.listProductsForAdmin(keyword, categoryId);
        List<Category> categories = productService.getAllCategories();

        req.setAttribute("products", products);
        req.setAttribute("categories", categories);
        req.setAttribute("searchKeyword", keyword);
        req.setAttribute("selectedCategory", categoryId);

        req.getRequestDispatcher("/admin/products.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();

        if ("/admin/products/save".equals(path)) {
            try {
                Product p = new Product();
                String idStr = req.getParameter("productId");
                if (idStr != null && !idStr.trim().isEmpty()) {
                    p.setProductId(Integer.parseInt(idStr.trim()));
                }
                p.setCategoryId(Integer.parseInt(req.getParameter("categoryId")));
                p.setName(req.getParameter("name"));
                p.setBrand(req.getParameter("brand"));
                p.setDescription(req.getParameter("description"));
                p.setPrice(new BigDecimal(req.getParameter("price")));
                p.setDiscount(new BigDecimal(req.getParameter("discount")));
                p.setStock(Integer.parseInt(req.getParameter("stock")));
                p.setImageUrl(req.getParameter("imageUrl"));
                p.setStatus(req.getParameter("status"));

                boolean saved = productService.saveProduct(p);
                if (saved) {
                    resp.sendRedirect(req.getContextPath() + "/admin/products?msg=" + URLEncoder.encode("Product saved successfully.", "UTF-8"));
                } else {
                    resp.sendRedirect(req.getContextPath() + "/admin/products?error=" + URLEncoder.encode("Failed to save product.", "UTF-8"));
                }
            } catch (Exception e) {
                resp.sendRedirect(req.getContextPath() + "/admin/products?error=" + URLEncoder.encode("Invalid input format.", "UTF-8"));
            }
        }
    }
}
