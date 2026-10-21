package com.shopsphere.controller;

import com.shopsphere.service.UserService;
import com.shopsphere.util.ValidationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;

@WebServlet(name = "RegisterServlet", urlPatterns = {"/register"})
public class RegisterServlet extends HttpServlet {

    private UserService userService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.userService = new UserService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String csrfToken = ValidationUtil.generateToken();
        req.getSession(true).setAttribute("csrfToken", csrfToken);
        req.getRequestDispatcher("/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = req.getParameter("name");
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String confirmPassword = req.getParameter("confirmPassword");
        String mobile = req.getParameter("mobile");
        String formCsrf = req.getParameter("csrfToken");

        String sessionCsrf = (String) req.getSession().getAttribute("csrfToken");
        if (sessionCsrf == null || !sessionCsrf.equals(formCsrf)) {
            resp.sendRedirect(req.getContextPath() + "/register?error=" + URLEncoder.encode("Invalid CSRF token or session expired.", "UTF-8"));
            return;
        }

        String error = userService.registerCustomer(name, email, password, confirmPassword, mobile);
        if (error == null) {
            resp.sendRedirect(req.getContextPath() + "/login?msg=" +
                    URLEncoder.encode("Account created successfully! Please sign in with your credentials.", "UTF-8"));
        } else {
            req.setAttribute("error", error);
            req.setAttribute("name", name);
            req.setAttribute("email", email);
            req.setAttribute("mobile", mobile);
            req.getRequestDispatcher("/register.jsp").forward(req, resp);
        }
    }
}
