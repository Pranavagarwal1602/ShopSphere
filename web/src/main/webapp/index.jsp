<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<c:set var="pageTitle" value="ShopSphere | Modern Enterprise E-Commerce" scope="request"/>
<jsp:include page="/fragments/header.jsp"/>
<jsp:include page="/fragments/navbar.jsp"/>

<main class="py-4">
    <div class="container">
        <!-- Hero Section -->
        <div class="ss-hero">
            <div class="row align-items-center">
                <div class="col-lg-7">
                    <span class="badge bg-primary px-3 py-2 rounded-pill mb-3 fw-bold text-uppercase letter-spacing-1">
                        RTU Advanced Java Capstone Edition
                    </span>
                    <h1 class="display-4 fw-extrabold mb-3">Discover Curated Essentials &amp; High-End Tech</h1>
                    <p class="lead text-light mb-4">
                        Experience lightning-fast browsing, persistent database shopping carts, atomic order checkout, 
                        and desktop admin synchronization.
                    </p>
                    <div class="d-flex flex-wrap gap-3">
                        <a href="${pageContext.request.contextPath}/products" class="btn btn-ss-primary btn-lg px-4">
                            <i class="bi bi-bag-fill me-2"></i> Explore Store
                        </a>
                        <a href="${pageContext.request.contextPath}/demo/network-rmi-guide.jsp" class="btn btn-outline-light btn-lg px-4">
                            <i class="bi bi-hdd-network me-2"></i> RMI &amp; Sockets
                        </a>
                    </div>
                </div>
                <div class="col-lg-5 d-none d-lg-block text-center">
                    <img src="https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=600" 
                         alt="Featured Gadget" class="img-fluid rounded-4 shadow-lg border border-secondary"
                         style="max-height: 280px; object-fit: cover;">
                </div>
            </div>
        </div>

        <!-- Category Cards Grid -->
        <div class="mb-5">
            <div class="d-flex justify-content-between align-items-center mb-4">
                <h3 class="fw-bold mb-0">Browse by Department</h3>
                <a href="${pageContext.request.contextPath}/products" class="text-decoration-none fw-semibold">View All <i class="bi bi-arrow-right"></i></a>
            </div>
            <div class="row g-3">
                <c:forEach items="${categories}" var="cat">
                    <div class="col-lg-3 col-md-6 col-6">
                        <a href="${pageContext.request.contextPath}/products?category=${cat.categoryId}" class="text-decoration-none">
                            <div class="card h-100 border-0 shadow-sm rounded-4 p-3 hover-shadow transition">
                                <div class="d-flex align-items-center gap-3">
                                    <div class="bg-primary-subtle text-primary p-3 rounded-circle fs-4">
                                        <i class="bi bi-grid-fill"></i>
                                    </div>
                                    <div>
                                        <h6 class="fw-bold mb-1 text-dark">${cat.name}</h6>
                                        <small class="text-secondary text-truncate d-block" style="max-width: 140px;">${cat.description}</small>
                                    </div>
                                </div>
                            </div>
                        </a>
                    </div>
                </c:forEach>
            </div>
        </div>

        <!-- Promotional Coupons Banner -->
        <c:if test="${not empty activeCoupons}">
            <div class="alert alert-primary rounded-4 p-4 shadow-sm mb-5 border-0 bg-primary-subtle">
                <div class="d-flex align-items-center justify-content-between flex-wrap gap-3">
                    <div class="d-flex align-items-center gap-3">
                        <i class="bi bi-ticket-perforated-fill text-primary fs-1"></i>
                        <div>
                            <h5 class="fw-bold mb-1 text-primary">Special Limited-Time Promotional Offers!</h5>
                            <p class="mb-0 text-secondary">Apply verified discount vouchers during checkout for instant savings.</p>
                        </div>
                    </div>
                    <div class="d-flex gap-2 flex-wrap">
                        <c:forEach items="${activeCoupons}" var="cpn">
                            <span class="badge bg-white text-dark border border-primary px-3 py-2 rounded-pill font-monospace fw-bold">
                                <i class="bi bi-tag-fill text-primary me-1"></i> ${cpn.code} 
                                (${cpn.discountType == 'PERCENTAGE' ? cpn.discountValue : '₹'.concat(cpn.discountValue)} OFF)
                            </span>
                        </c:forEach>
                    </div>
                </div>
            </div>
        </c:if>

        <!-- Featured Products Section -->
        <div class="mb-5">
            <div class="d-flex justify-content-between align-items-center mb-4">
                <div>
                    <h3 class="fw-bold mb-1">Featured Essentials</h3>
                    <p class="text-muted mb-0">Handpicked items loaded directly from the MySQL database</p>
                </div>
                <a href="${pageContext.request.contextPath}/products" class="btn btn-outline-primary btn-sm rounded-pill px-3">
                    View Catalog
                </a>
            </div>

            <div class="row">
                <c:forEach items="${featuredProducts}" var="prod">
                    <tags:productCard product="${prod}"/>
                </c:forEach>
            </div>
        </div>

        <!-- Hot Discounts Section -->
        <c:if test="${not empty discountedProducts}">
            <div class="mb-5">
                <div class="d-flex justify-content-between align-items-center mb-4">
                    <div>
                        <h3 class="fw-bold mb-1"><i class="bi bi-fire text-danger me-2"></i>Special Price Drops</h3>
                        <p class="text-muted mb-0">Grab the highest product discounts available today</p>
                    </div>
                </div>
                <div class="row">
                    <c:forEach items="${discountedProducts}" var="prod">
                        <tags:productCard product="${prod}"/>
                    </c:forEach>
                </div>
            </div>
        </c:if>
    </div>
</main>

<jsp:include page="/fragments/footer.jsp"/>
