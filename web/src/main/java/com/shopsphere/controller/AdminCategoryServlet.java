package com.shopsphere.controller;

import com.shopsphere.model.Category;
import com.shopsphere.service.ProductService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

@WebServlet(name = "AdminCategoryServlet", urlPatterns = {"/admin/categories", "/admin/categories/save", "/admin/categories/delete"})
public class AdminCategoryServlet extends HttpServlet {

    private ProductService productService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.productService = new ProductService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Category> categories = productService.getAllCategories();
        req.setAttribute("categories", categories);
        req.getRequestDispatcher("/admin/categories.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();

        if ("/admin/categories/save".equals(path)) {
            try {
                Category c = new Category();
                String idStr = req.getParameter("categoryId");
                if (idStr != null && !idStr.trim().isEmpty()) {
                    c.setCategoryId(Integer.parseInt(idStr.trim()));
                }
                c.setName(req.getParameter("name"));
                c.setDescription(req.getParameter("description"));
                c.setStatus(req.getParameter("status"));

                boolean saved = productService.saveCategory(c);
                if (saved) {
                    resp.sendRedirect(req.getContextPath() + "/admin/categories?msg=" + URLEncoder.encode("Category saved successfully.", "UTF-8"));
                } else {
                    resp.sendRedirect(req.getContextPath() + "/admin/categories?error=" + URLEncoder.encode("Failed to save category.", "UTF-8"));
                }
            } catch (Exception e) {
                resp.sendRedirect(req.getContextPath() + "/admin/categories?error=" + URLEncoder.encode("Invalid input format.", "UTF-8"));
            }
        } else if ("/admin/categories/delete".equals(path)) {
            try {
                int catId = Integer.parseInt(req.getParameter("categoryId"));
                boolean deleted = productService.deleteCategory(catId);
                if (deleted) {
                    resp.sendRedirect(req.getContextPath() + "/admin/categories?msg=" + URLEncoder.encode("Category deleted.", "UTF-8"));
                } else {
                    resp.sendRedirect(req.getContextPath() + "/admin/categories?error=" +
                            URLEncoder.encode("Cannot delete category because active products are assigned to it.", "UTF-8"));
                }
            } catch (Exception e) {
                resp.sendRedirect(req.getContextPath() + "/admin/categories");
            }
        }
    }
}
