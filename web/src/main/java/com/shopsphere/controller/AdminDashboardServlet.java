package com.shopsphere.controller;

import com.shopsphere.model.Order;
import com.shopsphere.model.Product;
import com.shopsphere.service.OrderService;
import com.shopsphere.service.ProductService;
import com.shopsphere.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@WebServlet(name = "AdminDashboardServlet", urlPatterns = {"/admin/dashboard"})
public class AdminDashboardServlet extends HttpServlet {

    private ProductService productService;
    private OrderService orderService;
    private UserService userService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.productService = new ProductService();
        this.orderService = new OrderService();
        this.userService = new UserService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int totalProducts = productService.countTotalProducts();
        int totalCustomers = userService.countTotalCustomers();
        int totalOrders = orderService.countTotalOrders();
        int pendingOrders = orderService.countPendingOrders();
        BigDecimal totalRevenue = orderService.getTotalSalesRevenue();

        List<Order> recentOrders = orderService.listAllOrders("ALL");
        if (recentOrders.size() > 5) {
            recentOrders = recentOrders.subList(0, 5);
        }

        List<Product> lowStockItems = productService.getLowStockProducts(20);

        req.setAttribute("totalProducts", totalProducts);
        req.setAttribute("totalCustomers", totalCustomers);
        req.setAttribute("totalOrders", totalOrders);
        req.setAttribute("pendingOrders", pendingOrders);
        req.setAttribute("totalRevenue", totalRevenue);
        req.setAttribute("recentOrders", recentOrders);
        req.setAttribute("lowStockItems", lowStockItems);

        req.getRequestDispatcher("/admin/dashboard.jsp").forward(req, resp);
    }
}
