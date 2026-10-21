package com.shopsphere.controller;

import com.shopsphere.model.Coupon;
import com.shopsphere.service.CouponService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.sql.Date;
import java.util.List;

@WebServlet(name = "AdminCouponServlet", urlPatterns = {"/admin/coupons", "/admin/coupons/save", "/admin/coupons/delete"})
public class AdminCouponServlet extends HttpServlet {

    private CouponService couponService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.couponService = new CouponService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Coupon> coupons = couponService.getAllCoupons();
        req.setAttribute("coupons", coupons);
        req.getRequestDispatcher("/admin/coupons.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();

        if ("/admin/coupons/save".equals(path)) {
            try {
                Coupon c = new Coupon();
                String idStr = req.getParameter("couponId");
                if (idStr != null && !idStr.trim().isEmpty()) {
                    c.setCouponId(Integer.parseInt(idStr.trim()));
                }
                c.setCode(req.getParameter("code"));
                c.setDiscountType(req.getParameter("discountType"));
                c.setDiscountValue(new BigDecimal(req.getParameter("discountValue")));
                c.setMinimumOrder(new BigDecimal(req.getParameter("minimumOrder")));
                c.setMaximumDiscount(new BigDecimal(req.getParameter("maximumDiscount")));
                c.setExpiryDate(Date.valueOf(req.getParameter("expiryDate")));
                c.setStatus(req.getParameter("status"));

                boolean ok = couponService.saveCoupon(c);
                if (ok) {
                    resp.sendRedirect(req.getContextPath() + "/admin/coupons?msg=" + URLEncoder.encode("Coupon saved.", "UTF-8"));
                } else {
                    resp.sendRedirect(req.getContextPath() + "/admin/coupons?error=" + URLEncoder.encode("Failed to save coupon.", "UTF-8"));
                }
            } catch (Exception e) {
                resp.sendRedirect(req.getContextPath() + "/admin/coupons?error=" + URLEncoder.encode("Invalid coupon input parameters.", "UTF-8"));
            }
        } else if ("/admin/coupons/delete".equals(path)) {
            try {
                int couponId = Integer.parseInt(req.getParameter("couponId"));
                couponService.deleteCoupon(couponId);
                resp.sendRedirect(req.getContextPath() + "/admin/coupons?msg=" + URLEncoder.encode("Coupon deleted.", "UTF-8"));
            } catch (Exception e) {
                resp.sendRedirect(req.getContextPath() + "/admin/coupons");
            }
        }
    }
}
