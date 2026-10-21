package com.shopsphere.controller;

import com.shopsphere.model.User;
import com.shopsphere.service.UserService;
import com.shopsphere.util.ValidationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Optional;

@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

    private UserService userService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.userService = new UserService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("currentUser") != null) {
            User current = (User) session.getAttribute("currentUser");
            if (current.isAdmin()) {
                resp.sendRedirect(req.getContextPath() + "/admin/dashboard");
            } else {
                resp.sendRedirect(req.getContextPath() + "/home");
            }
            return;
        }

        // Generate CSRF token for the login form
        String csrfToken = ValidationUtil.generateToken();
        req.getSession(true).setAttribute("csrfToken", csrfToken);

        req.getRequestDispatcher("/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String formCsrf = req.getParameter("csrfToken");

        HttpSession session = req.getSession(false);
        String sessionCsrf = (session != null) ? (String) session.getAttribute("csrfToken") : null;

        if (sessionCsrf == null || !sessionCsrf.equals(formCsrf)) {
            resp.sendRedirect(req.getContextPath() + "/login?error=" + URLEncoder.encode("Session timed out or invalid CSRF token. Please retry.", "UTF-8"));
            return;
        }

        Optional<User> authUser = userService.authenticate(email, password);
        if (authUser.isPresent()) {
            User user = authUser.get();

            // Session Fixation Prevention: invalidate old session and create a brand new one
            session.invalidate();
            HttpSession newSession = req.getSession(true);
            newSession.setAttribute("currentUser", user);
            newSession.setMaxInactiveInterval(30 * 60); // 30 minutes

            String redirect = (String) req.getSession().getAttribute("redirectAfterLogin");
            if (redirect != null && !redirect.isEmpty()) {
                req.getSession().removeAttribute("redirectAfterLogin");
                resp.sendRedirect(redirect);
                return;
            }

            if (user.isAdmin()) {
                resp.sendRedirect(req.getContextPath() + "/admin/dashboard");
            } else {
                resp.sendRedirect(req.getContextPath() + "/home?msg=" + URLEncoder.encode("Welcome back, " + user.getName() + "!", "UTF-8"));
            }
        } else {
            resp.sendRedirect(req.getContextPath() + "/login?error=" + URLEncoder.encode("Invalid email or password. Please try again.", "UTF-8"));
        }
    }
}
