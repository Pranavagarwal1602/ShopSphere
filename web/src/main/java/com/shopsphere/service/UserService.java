package com.shopsphere.service;

import com.shopsphere.dao.UserDAO;
import com.shopsphere.model.User;
import com.shopsphere.util.PasswordUtil;
import com.shopsphere.util.ValidationUtil;
import java.util.List;
import java.util.Optional;

public class UserService {
    private final UserDAO userDAO;

    public UserService() {
        this.userDAO = new UserDAO();
    }

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public Optional<User> authenticate(String email, String plainPassword) {
        if (!ValidationUtil.isValidEmail(email) || plainPassword == null || plainPassword.isEmpty()) {
            return Optional.empty();
        }

        Optional<User> userOpt = userDAO.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if ("ACTIVE".equalsIgnoreCase(user.getStatus())) {
                if (PasswordUtil.checkPassword(plainPassword, user.getPassword())) {
                    return Optional.of(user);
                }
            }
        }
        return Optional.empty();
    }

    public String registerCustomer(String name, String email, String password, String confirmPassword, String mobile) {
        if (name == null || name.trim().length() < 2) {
            return "Name must be at least 2 characters long.";
        }
        if (!ValidationUtil.isValidEmail(email)) {
            return "Please provide a valid email address.";
        }
        if (!ValidationUtil.isStrongPassword(password)) {
            return "Password must be at least 6 characters long.";
        }
        if (!password.equals(confirmPassword)) {
            return "Password and confirmation do not match.";
        }
        if (mobile != null && !mobile.trim().isEmpty() && !ValidationUtil.isValidMobile(mobile)) {
            return "Mobile number must be 10 digits.";
        }

        if (userDAO.findByEmail(email).isPresent()) {
            return "An account with this email address already exists.";
        }

        String hashedPassword = PasswordUtil.hashPassword(password);
        User user = new User();
        user.setName(name.trim());
        user.setEmail(email.trim().toLowerCase());
        user.setPassword(hashedPassword);
        user.setMobile(mobile != null ? mobile.trim() : "");
        user.setRole("CUSTOMER");
        user.setStatus("ACTIVE");

        boolean created = userDAO.create(user);
        return created ? null : "Registration failed due to a database error. Please try again.";
    }

    public Optional<User> getUserById(int userId) {
        return userDAO.findById(userId);
    }

    public boolean updateProfile(int userId, String name, String mobile) {
        if (name == null || name.trim().length() < 2) return false;
        User user = new User();
        user.setUserId(userId);
        user.setName(name.trim());
        user.setMobile(mobile != null ? mobile.trim() : "");
        return userDAO.updateProfile(user);
    }

    public boolean changePassword(int userId, String oldPassword, String newPassword) {
        if (!ValidationUtil.isStrongPassword(newPassword)) return false;
        Optional<User> userOpt = userDAO.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (PasswordUtil.checkPassword(oldPassword, user.getPassword())) {
                String hashedNew = PasswordUtil.hashPassword(newPassword);
                return userDAO.updatePassword(userId, hashedNew);
            }
        }
        return false;
    }

    public List<User> listUsers(String search) {
        return userDAO.findAll(search);
    }

    public boolean setUserStatus(int userId, String status) {
        return userDAO.updateStatus(userId, status);
    }

    public int countTotalCustomers() {
        return userDAO.countTotalCustomers();
    }
}
