package com.shopsphere.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * Enterprise Database Connection Manager.
 * Supports dual connection modes:
 * 1. JNDI DataSource (Production / Tomcat container context: java:comp/env/jdbc/ShopSphereDB)
 * 2. Standalone JDBC Driver Manager (Direct connection via environment variables or db.properties)
 *
 * Demonstrates RTU syllabus requirements: JDBC Driver loading, java.sql.Connection,
 * PreparedStatement management, JNDI lookup, and ResourceBundle/Properties config.
 */
public class DBConnectionUtil {
    private static final Logger LOGGER = Logger.getLogger(DBConnectionUtil.class.getName());

    private static String jdbcUrl;
    private static String dbUser;
    private static String dbPassword;
    private static String driverClassName;
    private static boolean useJndi = false;
    private static String jndiName = "java:comp/env/jdbc/ShopSphereDB";

    static {
        loadConfiguration();
    }

    private static void loadConfiguration() {
        Properties props = new Properties();
        try (InputStream in = DBConnectionUtil.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (in != null) {
                props.load(in);
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "db.properties not found in classpath, falling back to defaults/env: " + e.getMessage());
        }

        // Environment variables override properties file for cloud 12-factor compliance
        driverClassName = getEnvOrProp("DB_DRIVER", props.getProperty("db.driver", "com.mysql.cj.jdbc.Driver"));
        jdbcUrl = getEnvOrProp("DB_URL", props.getProperty("db.url", "jdbc:mysql://localhost:3306/shopsphere?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8"));
        dbUser = getEnvOrProp("DB_USER", props.getProperty("db.user", "root"));
        dbPassword = getEnvOrProp("DB_PASSWORD", props.getProperty("db.password", "root"));
        useJndi = Boolean.parseBoolean(getEnvOrProp("DB_USE_JNDI", props.getProperty("db.use.jndi", "false")));
        jndiName = getEnvOrProp("DB_JNDI_NAME", props.getProperty("db.jndi.name", "java:comp/env/jdbc/ShopSphereDB"));

        try {
            Class.forName(driverClassName);
            LOGGER.info("MySQL JDBC Driver successfully loaded: " + driverClassName);
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Failed to load JDBC driver: " + driverClassName, e);
        }
    }

    private static String getEnvOrProp(String envKey, String fallback) {
        String envVal = System.getenv(envKey);
        if (envVal != null && !envVal.trim().isEmpty()) {
            return envVal.trim();
        }
        return fallback;
    }

    /**
     * Obtains a thread-safe SQL connection.
     * Tries JNDI DataSource first if enabled, otherwise uses DriverManager.
     * Caller is responsible for closing the connection (typically via try-with-resources).
     */
    public static Connection getConnection() throws SQLException {
        if (useJndi) {
            try {
                Context initContext = new InitialContext();
                DataSource ds = (DataSource) initContext.lookup(jndiName);
                if (ds != null) {
                    return ds.getConnection();
                }
            } catch (NamingException e) {
                LOGGER.log(Level.FINE, "JNDI DataSource not available, falling back to DriverManager: " + e.getMessage());
            }
        }
        return DriverManager.getConnection(jdbcUrl, dbUser, dbPassword);
    }

    /**
     * Convenient helper to safely test connectivity.
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Database connectivity test failed: " + e.getMessage());
            return false;
        }
    }

    public static String getJdbcUrl() { return jdbcUrl; }
    public static String getDbUser() { return dbUser; }
}
