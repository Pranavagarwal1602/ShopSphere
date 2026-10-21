# ShopSphere: RTU Advanced Java Capstone Report & Viva Guide

## 1. Project Report Outline

### Chapter 1: Introduction
- **1.1 Background:** The exponential growth of e-commerce platforms and the necessity of multi-tier, robust, ACID-compliant backends.
- **1.2 Problem Statement:** Developing an enterprise online shopping architecture capable of handling persistent cart sessions, concurrent stock mutations, and multi-channel synchronization (Web, Desktop, and Network sockets).
- **1.3 Project Objectives:**
  - Build a secure MVC web application using Jakarta Servlet 6.1 and JSP 3.1.
  - Implement full ACID multi-table database transactions with MySQL JDBC.
  - Deliver desktop administration using Java Swing MVC.
  - Implement distributed computing modules using Java RMI and TCP Sockets.
  - Fulfill all RTU Advanced Java syllabus requirements.

### Chapter 2: Technology Stack & Architectural Specifications
- **2.1 N-Tier Architecture:** Client Tier &rarr; Web Tier &rarr; Service Tier &rarr; Data Access Tier &rarr; Database.
- **2.2 Core Technologies:**
  - Jakarta Servlet 6.1, JSP 3.1, JSTL 3.0, Expression Language (EL).
  - MySQL Connector/J 9.1, BCrypt Password Encryption (`jbcrypt`).
  - Java Swing (`JFrame`, `JTable`, `JTabbedPane`), Java Sockets (`ServerSocket`), Java RMI.
  - Bootstrap 5.3.3 & Custom Responsive Vanilla CSS.

### Chapter 3: Database Design & Normalization
- **3.1 3NF Normalization:** Detailed analysis of all 12 tables ensuring zero data redundancy.
- **3.2 Foreign Key Constraints:** Cascade deletions on user carts and wishlists; RESTRICT deletions on active product categories.
- **3.3 Entity-Relationship (ER) Documentation:** Relationships between Users, Addresses, Products, Orders, Order Items, Payments, Coupons, and Reviews.

### Chapter 4: Module Implementation & Code Organization
- **4.1 Customer Storefront:** Category catalog browsing, dynamic multi-parameter search/filters, cookie tracking of recently viewed items.
- **4.2 Shopping Cart & Checkout:** Database-persisted cart state, server-side coupon validation, and atomic multi-table checkout transactions.
- **4.3 Security & Role-Based Access Control:** `AuthenticationFilter`, `AdminFilter`, CSRF tokens, session fixation mitigation.
- **4.4 Administrative Console:** CRUD operations on products and categories, low-stock alerts, order status transitions.
- **4.5 RTU Syllabus Modules:** JSTL SQL & XML tags demo, traditional JSP scriptlet/expression comparisons, Applet lifecycle evolution.
- **4.6 Distributed Modules:** Swing Desktop Admin, Multi-threaded Socket Inventory Daemon, Java RMI Remote Registry Service.

### Chapter 5: Testing & Verification
- **5.1 Unit Testing:** JUnit 5 test cases verifying BCrypt hashing, price calculations, cart subtotals, and coupon validations.
- **5.2 Transaction Failure & Rollback Verification:** Simulating stock conflicts during checkout to prove atomic rollback.

### Chapter 6: Conclusion & Future Scope
- **6.1 Conclusion:** Successful implementation of an academic capstone matching professional e-commerce quality.
- **6.2 Future Enhancements:** Integration of real payment gateways (Razorpay/Stripe), ElasticSearch indexing, and microservice decoupling.

---

## 2. RTU Advanced Java Viva Questions & Answers

### Q1: What is the fundamental difference between `Statement` and `PreparedStatement` in JDBC?
**Answer:** `PreparedStatement` pre-compiles SQL queries on the database engine, providing significantly faster execution for repetitive queries and using parameter placeholders (`?`) that completely eliminate SQL Injection vulnerabilities. `Statement` compiles queries on every execution and requires unsafe string concatenation.

### Q2: How does ShopSphere protect user passwords? Why not use MD5 or plain text?
**Answer:** ShopSphere utilizes **BCrypt** with salted cryptographic hashing (`PasswordUtil.hashPassword`). Plain text exposes users during database leaks, while MD5 and SHA-1 are broken algorithms vulnerable to rainbow tables. BCrypt introduces a configurable work factor (log rounds) and unique salt per user to prevent brute-force attacks.

### Q3: Why is JNDI DataSource preferred over `DriverManager` in enterprise production?
**Answer:** `DriverManager` opens a new physical database connection per request, which is computationally expensive. A JNDI DataSource (`java:comp/env/jdbc/ShopSphereDB`) maintains a pre-allocated **Connection Pool** managed by the servlet container (Tomcat), enabling connection reuse and higher concurrency.

### Q4: Explain the lifecycle of a Jakarta Servlet.
**Answer:** 
1. **Loading and Instantiation:** Container loads the servlet class.
2. **`init(ServletConfig config)`:** Invoked once to initialize resources.
3. **`service(HttpServletRequest, HttpServletResponse)`:** Invoked for every incoming request; dispatches to `doGet()`, `doPost()`, etc.
4. **`destroy()`:** Invoked once before container unloads the servlet for clean garbage collection.

### Q5: What is the difference between `HttpSession` and Cookies?
**Answer:** `HttpSession` stores state on the server (memory/database) and assigns a unique `JSESSIONID` cookie to the client. Cookies store non-sensitive data directly on the client's browser (e.g., ShopSphere's recently viewed products cookie `recent_products`).

### Q6: Why did you implement filters (`AuthenticationFilter` and `AdminFilter`)?
**Answer:** Servlet filters intercept incoming HTTP requests before reaching target servlets. They enforce centralized security, validating user session existence and admin role permissions without duplicating authentication checks in every individual servlet.

### Q7: Why are Java Applets obsolete, and what replaced them?
**Answer:** Applets required a client-side browser plugin (NPAPI) with major security vulnerabilities and slow startup times. Modern web architectures replaced Applets with standard server-side rendering (Servlets, JSP, EL) and clientside HTML5/CSS3/JavaScript.

### Q8: How does Java RMI work in the ShopSphere project?
**Answer:** RMI allows a client JVM to invoke methods on an object residing in a remote server JVM. In ShopSphere:
1. `InventoryRemoteService` defines the remote interface extending `Remote`.
2. `InventoryRemoteServiceImpl` implements it extending `UnicastRemoteObject`.
3. `RmiServerMain` registers the service into the `rmiregistry` on port `1099`.
4. `RmiClientMain` looks up the stub via `LocateRegistry.getRegistry().lookup()` and invokes methods transparently over TCP.

### Q9: How is an ACID transaction achieved in JDBC for Checkout?
**Answer:** 
1. Obtain connection: `conn.setAutoCommit(false);`
2. Execute insert order, insert order items, decrement product stock, and insert payment.
3. If all succeed: `conn.commit();`
4. If any SQL exception or stock conflict arises: `conn.rollback();` in the catch block.
5. In `finally`, restore `conn.setAutoCommit(true);` and close connection.

### Q10: Why use Expression Language (EL) instead of JSP Scriptlets?
**Answer:** EL (`${user.name}`) provides a concise, clean syntax without verbose Java syntax (`<%= user.getName() %>`), handles `null` values gracefully without throwing `NullPointerException`, and ensures strict separation of frontend presentation from backend logic.
