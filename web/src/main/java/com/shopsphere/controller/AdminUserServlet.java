package com.shopsphere.controller;

import com.shopsphere.model.User;
import com.shopsphere.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

@WebServlet(name = "AdminUserServlet", urlPatterns = {"/admin/users", "/admin/users/toggle-status"})
public class AdminUserServlet extends HttpServlet {

    private UserService userService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.userService = new UserService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String keyword = req.getParameter("q");
        List<User> users = userService.listUsers(keyword);
        req.setAttribute("users", users);
        req.setAttribute("searchKeyword", keyword);
        req.getRequestDispatcher("/admin/users.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            int userId = Integer.parseInt(req.getParameter("userId"));
            String status = req.getParameter("status");
            userService.setUserStatus(userId, status);
            resp.sendRedirect(req.getContextPath() + "/admin/users?msg=" + URLEncoder.encode("User status updated.", "UTF-8"));
        } catch (Exception e) {
            resp.sendRedirect(req.getContextPath() + "/admin/users");
        }
    }
}
