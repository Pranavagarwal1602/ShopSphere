package com.shopsphere.filter;

import com.shopsphere.model.User;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Filter protecting customer routes: /cart, /checkout, /orders, /profile, /wishlist, /address
 */
@WebFilter(filterName = "AuthenticationFilter", urlPatterns = {
        "/cart/*", "/checkout/*", "/orders/*", "/profile/*", "/wishlist/*", "/address/*", "/review/submit"
})
public class AuthenticationFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("currentUser") : null;

        if (user == null) {
            String redirectUrl = req.getRequestURI();
            if (req.getQueryString() != null) {
                redirectUrl += "?" + req.getQueryString();
            }
            req.getSession(true).setAttribute("redirectAfterLogin", redirectUrl);
            resp.sendRedirect(req.getContextPath() + "/login?error=" + java.net.URLEncoder.encode("Please log in to continue.", "UTF-8"));
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {}
}
