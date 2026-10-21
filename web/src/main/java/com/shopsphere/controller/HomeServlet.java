package com.shopsphere.controller;

import com.shopsphere.model.Category;
import com.shopsphere.model.Coupon;
import com.shopsphere.model.Product;
import com.shopsphere.service.CouponService;
import com.shopsphere.service.ProductService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "HomeServlet", urlPatterns = {"", "/home", "/index"})
public class HomeServlet extends HttpServlet {

    private ProductService productService;
    private CouponService couponService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.productService = new ProductService();
        this.couponService = new CouponService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Category> categories = productService.getActiveCategories();
        List<Product> featured = productService.getFeaturedProducts(8);
        List<Product> discounted = productService.getDiscountedProducts(4);
        List<Product> topRated = productService.getTopRatedProducts(4);
        List<Coupon> activeCoupons = couponService.getCustomerAvailableCoupons();

        req.setAttribute("categories", categories);
        req.setAttribute("featuredProducts", featured);
        req.setAttribute("discountedProducts", discounted);
        req.setAttribute("topRatedProducts", topRated);
        req.setAttribute("activeCoupons", activeCoupons);

        req.getRequestDispatcher("/index.jsp").forward(req, resp);
    }
}
