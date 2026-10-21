package com.shopsphere.listener;

import com.shopsphere.util.DBConnectionUtil;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

/**
 * Demonstrates RTU Syllabus requirements:
 * ServletContextListener, HttpSessionListener, application lifecycle events, and active session tracking.
 */
@WebListener
public class AppContextListener implements ServletContextListener, HttpSessionListener {
    private static final Logger LOGGER = Logger.getLogger(AppContextListener.class.getName());
    private static final AtomicInteger ACTIVE_SESSIONS = new AtomicInteger(0);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext ctx = sce.getServletContext();
        ctx.setAttribute("appName", "ShopSphere");
        ctx.setAttribute("appVersion", "1.0.0-RTU-CAPSTONE");
        LOGGER.info("ShopSphere Application Initialized successfully on Servlet Container.");

        // Check DB connection on startup
        boolean dbOk = DBConnectionUtil.testConnection();
        ctx.setAttribute("databaseConnected", dbOk);
        LOGGER.info("Initial Database Health Check: " + (dbOk ? "ONLINE" : "OFFLINE (Verify db.properties/MySQL service)"));
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        LOGGER.info("ShopSphere Application Context is being destroyed cleanly.");
    }

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        int count = ACTIVE_SESSIONS.incrementAndGet();
        se.getSession().getServletContext().setAttribute("activeSessionsCount", count);
        LOGGER.fine("New HttpSession created: " + se.getSession().getId() + " | Total active: " + count);
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        int count = ACTIVE_SESSIONS.decrementAndGet();
        se.getSession().getServletContext().setAttribute("activeSessionsCount", count);
        LOGGER.fine("HttpSession destroyed: " + se.getSession().getId() + " | Remaining active: " + count);
    }
}
