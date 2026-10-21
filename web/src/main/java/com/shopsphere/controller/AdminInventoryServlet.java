package com.shopsphere.controller;

import com.shopsphere.model.Product;
import com.shopsphere.service.ProductService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

@WebServlet(name = "AdminInventoryServlet", urlPatterns = {"/admin/inventory", "/admin/inventory/update-stock"})
public class AdminInventoryServlet extends HttpServlet {

    private ProductService productService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.productService = new ProductService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String filter = req.getParameter("filter");
        List<Product> products;
        if ("low".equalsIgnoreCase(filter)) {
            products = productService.getLowStockProducts(20);
        } else {
            products = productService.listProductsForAdmin(null, null);
        }

        req.setAttribute("products", products);
        req.setAttribute("selectedFilter", filter);
        req.getRequestDispatcher("/admin/inventory.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            int productId = Integer.parseInt(req.getParameter("productId"));
            int newStock = Integer.parseInt(req.getParameter("stock"));
            boolean ok = productService.updateStock(productId, newStock);
            if (ok) {
                resp.sendRedirect(req.getContextPath() + "/admin/inventory?msg=" + URLEncoder.encode("Stock updated successfully.", "UTF-8"));
            } else {
                resp.sendRedirect(req.getContextPath() + "/admin/inventory?error=" + URLEncoder.encode("Failed to update stock.", "UTF-8"));
            }
        } catch (Exception e) {
            resp.sendRedirect(req.getContextPath() + "/admin/inventory");
        }
    }
}
