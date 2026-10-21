package com.shopsphere.controller;

import com.shopsphere.model.Order;
import com.shopsphere.model.User;
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

@WebServlet(name = "OrderServlet", urlPatterns = {"/orders", "/order-details", "/orders/cancel", "/order-success"})
public class OrderServlet extends HttpServlet {

    private OrderService orderService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.orderService = new OrderService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("currentUser");
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String path = req.getServletPath();

        if ("/order-success".equals(path)) {
            String orderIdParam = req.getParameter("orderId");
            try {
                int orderId = Integer.parseInt(orderIdParam);
                Optional<Order> orderOpt = orderService.getOrderForUser(orderId, user.getUserId());
                if (orderOpt.isPresent()) {
                    req.setAttribute("order", orderOpt.get());
                    req.getRequestDispatcher("/order-success.jsp").forward(req, resp);
                    return;
                }
            } catch (Exception ignored) {}
            resp.sendRedirect(req.getContextPath() + "/orders");
            return;
        }

        if ("/order-details".equals(path)) {
            String orderIdParam = req.getParameter("id");
            try {
                int orderId = Integer.parseInt(orderIdParam);
                Optional<Order> orderOpt = orderService.getOrderForUser(orderId, user.getUserId());
                if (orderOpt.isPresent()) {
                    req.setAttribute("order", orderOpt.get());
                    req.getRequestDispatcher("/order-details.jsp").forward(req, resp);
                    return;
                }
            } catch (Exception ignored) {}
            resp.sendRedirect(req.getContextPath() + "/orders?error=" + URLEncoder.encode("Order not found or unauthorized.", "UTF-8"));
            return;
        }

        // Default: /orders list
        List<Order> orders = orderService.getOrdersByUser(user.getUserId());
        req.setAttribute("orders", orders);
        req.getRequestDispatcher("/orders.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("currentUser");
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String path = req.getServletPath();
        if ("/orders/cancel".equals(path)) {
            try {
                int orderId = Integer.parseInt(req.getParameter("orderId"));
                boolean cancelled = orderService.cancelOrder(orderId, user.getUserId());
                if (cancelled) {
                    resp.sendRedirect(req.getContextPath() + "/order-details?id=" + orderId + "&msg=" + URLEncoder.encode("Order cancelled successfully. Restocked inventory.", "UTF-8"));
                } else {
                    resp.sendRedirect(req.getContextPath() + "/order-details?id=" + orderId + "&error=" + URLEncoder.encode("Order cannot be cancelled in its current state.", "UTF-8"));
                }
            } catch (Exception e) {
                resp.sendRedirect(req.getContextPath() + "/orders");
            }
        }
    }
}
