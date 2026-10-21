<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<footer class="ss-footer">
    <div class="container">
        <div class="row g-4">
            <!-- Brand & Mission -->
            <div class="col-lg-4 col-md-6">
                <h5 class="ss-footer-title d-flex align-items-center gap-2">
                    <i class="bi bi-bag-check-fill text-primary"></i>
                    ShopSphere
                </h5>
                <p class="text-secondary small">
                    An enterprise full-stack Java e-commerce system built for the RTU Advanced Java Capstone examination.
                    Demonstrating Jakarta Servlets 6.1, JSP, EL/JSTL, MySQL JDBC, RMI, Sockets, and Swing MVC.
                </p>
                <div class="d-flex gap-3 text-secondary">
                    <span class="badge bg-dark border border-secondary">Tomcat 11</span>
                    <span class="badge bg-dark border border-secondary">Jakarta Servlet 6.1</span>
                    <span class="badge bg-dark border border-secondary">MySQL 8.0</span>
                </div>
            </div>

            <!-- Quick Links -->
            <div class="col-lg-2 col-md-6">
                <h6 class="ss-footer-title">Shop</h6>
                <ul class="list-unstyled small">
                    <li class="mb-2"><a href="${pageContext.request.contextPath}/products">All Products</a></li>
                    <li class="mb-2"><a href="${pageContext.request.contextPath}/cart">My Cart</a></li>
                    <li class="mb-2"><a href="${pageContext.request.contextPath}/wishlist">Wishlist</a></li>
                    <li class="mb-2"><a href="${pageContext.request.contextPath}/orders">Order Tracking</a></li>
                </ul>
            </div>

            <!-- Syllabus Modules -->
            <div class="col-lg-3 col-md-6">
                <h6 class="ss-footer-title">Syllabus Demonstrations</h6>
                <ul class="list-unstyled small">
                    <li class="mb-2"><a href="${pageContext.request.contextPath}/demo/jstl-sql.jsp">JSTL SQL & XML Query Tags</a></li>
                    <li class="mb-2"><a href="${pageContext.request.contextPath}/demo/traditional-jsp.jsp">JSP Scriptlets vs Expression Language</a></li>
                    <li class="mb-2"><a href="${pageContext.request.contextPath}/demo/network-rmi-guide.jsp">Socket Daemon & RMI Services</a></li>
                    <li class="mb-2"><a href="${pageContext.request.contextPath}/demo/applet-history.jsp">Applet Architecture & Web Transition</a></li>
                </ul>
            </div>

            <!-- Security & System Info -->
            <div class="col-lg-3 col-md-6">
                <h6 class="ss-footer-title">System Status</h6>
                <ul class="list-unstyled small text-secondary">
                    <li class="mb-2"><i class="bi bi-shield-lock-fill text-success me-2"></i>BCrypt Hashing Enabled</li>
                    <li class="mb-2"><i class="bi bi-database-check text-info me-2"></i>ACID Transactions Verified</li>
                    <li class="mb-2"><i class="bi bi-activity text-warning me-2"></i>Active Sessions: ${empty applicationScope.activeSessionsCount ? 1 : applicationScope.activeSessionsCount}</li>
                    <li class="mb-2"><a href="${pageContext.request.contextPath}/admin/dashboard" class="text-primary fw-bold"><i class="bi bi-gear-fill me-1"></i> Admin Portal</a></li>
                </ul>
            </div>
        </div>

        <hr class="border-secondary my-4">

        <div class="row align-items-center">
            <div class="col-md-6 text-center text-md-start small text-secondary">
                &copy; 2026 ShopSphere Project. Built for RTU Advanced Java Syllabus Capstone.
            </div>
            <div class="col-md-6 text-center text-md-end small text-secondary mt-2 mt-md-0">
                <span>Designed with Bootstrap 5 &amp; Vanilla CSS</span>
            </div>
        </div>
    </div>
</footer>

<!-- Bootstrap 5 Bundle with Popper -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
