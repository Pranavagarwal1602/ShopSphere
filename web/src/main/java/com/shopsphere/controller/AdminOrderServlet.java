package com.shopsphere.controller;

import com.shopsphere.model.Order;
import com.shopsphere.service.OrderService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Optional;

@WebServlet(name = "AdminOrderServlet", urlPatterns = {"/admin/orders", "/admin/orders/details", "/admin/orders/update-status"})
public class AdminOrderServlet extends HttpServlet {

    private OrderService orderService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.orderService = new OrderService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();

        if ("/admin/orders/details".equals(path)) {
            try {
                int orderId = Integer.parseInt(req.getParameter("id"));
                Optional<Order> orderOpt = orderService.getOrderForAdmin(orderId);
                if (orderOpt.isPresent()) {
                    req.setAttribute("order", orderOpt.get());
                    req.getRequestDispatcher("/admin/order-details.jsp").forward(req, resp);
                    return;
                }
            } catch (Exception ignored) {}
            resp.sendRedirect(req.getContextPath() + "/admin/orders");
            return;
        }

        String statusFilter = req.getParameter("status");
        List<Order> orders = orderService.listAllOrders(statusFilter);
        req.setAttribute("orders", orders);
        req.setAttribute("selectedStatus", statusFilter);

        req.getRequestDispatcher("/admin/orders.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            int orderId = Integer.parseInt(req.getParameter("orderId"));
            String newStatus = req.getParameter("orderStatus");
            String paymentStatus = req.getParameter("paymentStatus");

            if (newStatus != null && !newStatus.trim().isEmpty()) {
                orderService.updateOrderStatus(orderId, newStatus);
            }
            if (paymentStatus != null && !paymentStatus.trim().isEmpty()) {
                orderService.updatePaymentStatus(orderId, paymentStatus);
            }

            resp.sendRedirect(req.getContextPath() + "/admin/orders/details?id=" + orderId + "&msg=" + URLEncoder.encode("Order updated.", "UTF-8"));
        } catch (Exception e) {
            resp.sendRedirect(req.getContextPath() + "/admin/orders");
        }
    }
}
