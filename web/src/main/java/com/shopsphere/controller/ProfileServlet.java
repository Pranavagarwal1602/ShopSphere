package com.shopsphere.controller;

import com.shopsphere.model.Address;
import com.shopsphere.model.User;
import com.shopsphere.service.AddressService;
import com.shopsphere.service.OrderService;
import com.shopsphere.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

@WebServlet(name = "ProfileServlet", urlPatterns = {"/profile", "/profile/update", "/profile/change-password", "/address/save", "/address/delete"})
public class ProfileServlet extends HttpServlet {

    private UserService userService;
    private AddressService addressService;
    private OrderService orderService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.userService = new UserService();
        this.addressService = new AddressService();
        this.orderService = new OrderService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("currentUser");
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        // Fresh profile info from DB
        userService.getUserById(user.getUserId()).ifPresent(u -> req.setAttribute("user", u));
        List<Address> addresses = addressService.getAddresses(user.getUserId());
        int orderCount = orderService.getOrdersByUser(user.getUserId()).size();

        req.setAttribute("addresses", addresses);
        req.setAttribute("orderCount", orderCount);

        req.getRequestDispatcher("/profile.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("currentUser");
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String path = req.getServletPath();

        if ("/profile/update".equals(path)) {
            String name = req.getParameter("name");
            String mobile = req.getParameter("mobile");
            boolean ok = userService.updateProfile(user.getUserId(), name, mobile);
            if (ok) {
                user.setName(name);
                user.setMobile(mobile);
                req.getSession().setAttribute("currentUser", user);
                resp.sendRedirect(req.getContextPath() + "/profile?msg=" + URLEncoder.encode("Profile updated successfully.", "UTF-8"));
            } else {
                resp.sendRedirect(req.getContextPath() + "/profile?error=" + URLEncoder.encode("Failed to update profile.", "UTF-8"));
            }
        } else if ("/profile/change-password".equals(path)) {
            String oldPass = req.getParameter("oldPassword");
            String newPass = req.getParameter("newPassword");
            boolean changed = userService.changePassword(user.getUserId(), oldPass, newPass);
            if (changed) {
                resp.sendRedirect(req.getContextPath() + "/profile?msg=" + URLEncoder.encode("Password changed successfully.", "UTF-8"));
            } else {
                resp.sendRedirect(req.getContextPath() + "/profile?error=" + URLEncoder.encode("Incorrect existing password or invalid new password (min 6 characters).", "UTF-8"));
            }
        } else if ("/address/save".equals(path)) {
            Address address = new Address();
            String idStr = req.getParameter("addressId");
            if (idStr != null && !idStr.trim().isEmpty()) {
                address.setAddressId(Integer.parseInt(idStr.trim()));
            }
            address.setUserId(user.getUserId());
            address.setAddressLine(req.getParameter("addressLine"));
            address.setCity(req.getParameter("city"));
            address.setState(req.getParameter("state"));
            address.setPincode(req.getParameter("pincode"));
            address.setAddressType(req.getParameter("addressType"));
            address.setDefault("on".equalsIgnoreCase(req.getParameter("isDefault")) || "true".equalsIgnoreCase(req.getParameter("isDefault")));

            String error = addressService.saveAddress(address);
            if (error == null) {
                resp.sendRedirect(req.getContextPath() + "/profile?msg=" + URLEncoder.encode("Address saved successfully.", "UTF-8"));
            } else {
                resp.sendRedirect(req.getContextPath() + "/profile?error=" + URLEncoder.encode(error, "UTF-8"));
            }
        } else if ("/address/delete".equals(path)) {
            try {
                int addressId = Integer.parseInt(req.getParameter("addressId"));
                addressService.deleteAddress(addressId, user.getUserId());
                resp.sendRedirect(req.getContextPath() + "/profile?msg=" + URLEncoder.encode("Address deleted.", "UTF-8"));
            } catch (Exception e) {
                resp.sendRedirect(req.getContextPath() + "/profile");
            }
        }
    }
}
