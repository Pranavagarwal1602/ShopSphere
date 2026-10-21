package com.shopsphere.controller;

import com.shopsphere.model.Category;
import com.shopsphere.model.Product;
import com.shopsphere.service.ProductService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@WebServlet(name = "ProductServlet", urlPatterns = {"/products"})
public class ProductServlet extends HttpServlet {

    private ProductService productService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.productService = new ProductService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String query = req.getParameter("q");
        String catParam = req.getParameter("category");
        String minPriceParam = req.getParameter("minPrice");
        String maxPriceParam = req.getParameter("maxPrice");
        String sort = req.getParameter("sort");
        String pageParam = req.getParameter("page");

        Integer categoryId = null;
        if (catParam != null && !catParam.trim().isEmpty()) {
            try { categoryId = Integer.parseInt(catParam.trim()); } catch (NumberFormatException ignored) {}
        }

        BigDecimal minPrice = null;
        if (minPriceParam != null && !minPriceParam.trim().isEmpty()) {
            try { minPrice = new BigDecimal(minPriceParam.trim()); } catch (Exception ignored) {}
        }

        BigDecimal maxPrice = null;
        if (maxPriceParam != null && !maxPriceParam.trim().isEmpty()) {
            try { maxPrice = new BigDecimal(maxPriceParam.trim()); } catch (Exception ignored) {}
        }

        int page = 1;
        if (pageParam != null && !pageParam.trim().isEmpty()) {
            try {
                page = Math.max(1, Integer.parseInt(pageParam.trim()));
            } catch (NumberFormatException ignored) {}
        }

        int pageSize = 9;
        List<Product> products = productService.searchCatalog(query, categoryId, minPrice, maxPrice, sort, page, pageSize);
        int totalProducts = productService.countCatalog(query, categoryId, minPrice, maxPrice);
        int totalPages = (int) Math.ceil((double) totalProducts / pageSize);
        List<Category> categories = productService.getActiveCategories();

        req.setAttribute("products", products);
        req.setAttribute("categories", categories);
        req.setAttribute("totalProducts", totalProducts);
        req.setAttribute("currentPage", page);
        req.setAttribute("totalPages", totalPages);
        req.setAttribute("query", query);
        req.setAttribute("selectedCategory", categoryId);
        req.setAttribute("minPrice", minPrice);
        req.setAttribute("maxPrice", maxPrice);
        req.setAttribute("selectedSort", sort);

        req.getRequestDispatcher("/products.jsp").forward(req, resp);
    }
}
