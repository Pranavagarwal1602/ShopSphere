<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<nav class="navbar navbar-expand-lg ss-navbar sticky-top">
    <div class="container">
        <!-- Brand Logo -->
        <a class="navbar-brand ss-brand" href="${pageContext.request.contextPath}/home">
            <i class="bi bi-bag-check-fill text-primary"></i>
            <span>ShopSphere</span>
            <span class="badge-sphere">Store</span>
        </a>

        <!-- Mobile Toggler -->
        <button class="navbar-toggler text-white border-0" type="button" data-bs-toggle="collapse" data-bs-target="#navbarMain"
                aria-controls="navbarMain" aria-expanded="false" aria-label="Toggle navigation">
            <i class="bi bi-list fs-2 text-white"></i>
        </button>

        <div class="collapse navbar-collapse" id="navbarMain">
            <!-- Search Bar in Navbar -->
            <form class="d-flex mx-lg-4 my-2 my-lg-0 flex-grow-1" action="${pageContext.request.contextPath}/products" method="GET">
                <div class="input-group">
                    <input class="form-control border-0 rounded-start-pill ps-3" type="search" name="q"
                           placeholder="Search smartphones, headphones, fashion, books..." value="${param.q}" aria-label="Search">
                    <button class="btn btn-light rounded-end-pill px-3 text-secondary" type="submit">
                        <i class="bi bi-search"></i>
                    </button>
                </div>
            </form>

            <!-- Navigation Links -->
            <ul class="navbar-nav ms-auto align-items-lg-center">
                <li class="nav-item">
                    <a class="nav-link ss-nav-link" href="${pageContext.request.contextPath}/products">
                        <i class="bi bi-grid-3x3-gap me-1"></i> Catalog
                    </a>
                </li>

                <!-- Syllabus Demonstrations Link -->
                <li class="nav-item dropdown">
                    <a class="nav-link ss-nav-link dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown">
                        <i class="bi bi-mortarboard-fill me-1"></i> RTU Syllabus Lab
                    </a>
                    <ul class="dropdown-menu dropdown-menu-dark shadow">
                        <li><a class="dropdown-item" href="${pageContext.request.contextPath}/demo/jstl-sql.jsp"><i class="bi bi-database me-2"></i>JSTL SQL & XML Tags</a></li>
                        <li><a class="dropdown-item" href="${pageContext.request.contextPath}/demo/traditional-jsp.jsp"><i class="bi bi-code-slash me-2"></i>Scriptlets & Expressions</a></li>
                        <li><a class="dropdown-item" href="${pageContext.request.contextPath}/demo/network-rmi-guide.jsp"><i class="bi bi-hdd-network me-2"></i>RMI & Socket Daemon Guide</a></li>
                        <li><a class="dropdown-item" href="${pageContext.request.contextPath}/demo/applet-history.jsp"><i class="bi bi-clock-history me-2"></i>Applet Lifecycle & Evolution</a></li>
                    </ul>
                </li>

                <c:choose>
                    <c:when test="${not empty sessionScope.currentUser}">
                        <!-- Wishlist -->
                        <li class="nav-item">
                            <a class="nav-link ss-nav-link" href="${pageContext.request.contextPath}/wishlist" title="Wishlist">
                                <i class="bi bi-heart me-1"></i> Wishlist
                            </a>
                        </li>

                        <!-- Persistent Cart Link -->
                        <li class="nav-item">
                            <a class="nav-link ss-nav-link position-relative" href="${pageContext.request.contextPath}/cart" title="Shopping Cart">
                                <i class="bi bi-cart3 fs-5"></i>
                                <span class="position-absolute top-1 start-100 translate-middle badge rounded-pill bg-danger">
                                    ${not empty sessionScope.cartCount ? sessionScope.cartCount : 'Cart'}
                                </span>
                            </a>
                        </li>

                        <!-- Customer Account Dropdown -->
                        <li class="nav-item dropdown ms-lg-2">
                            <a class="nav-link ss-nav-link dropdown-toggle d-flex align-items-center gap-2" href="#" role="button" data-bs-toggle="dropdown">
                                <i class="bi bi-person-circle fs-5"></i>
                                <span>${sessionScope.currentUser.name}</span>
                            </a>
                            <ul class="dropdown-menu dropdown-menu-end shadow">
                                <c:if test="${sessionScope.currentUser.role == 'ADMIN'}">
                                    <li><a class="dropdown-item text-primary fw-bold" href="${pageContext.request.contextPath}/admin/dashboard"><i class="bi bi-speedometer2 me-2"></i>Admin Dashboard</a></li>
                                    <li><hr class="dropdown-divider"></li>
                                </c:if>
                                <li><a class="dropdown-item" href="${pageContext.request.contextPath}/profile"><i class="bi bi-person me-2"></i>My Profile</a></li>
                                <li><a class="dropdown-item" href="${pageContext.request.contextPath}/orders"><i class="bi bi-bag-check me-2"></i>My Orders</a></li>
                                <li><hr class="dropdown-divider"></li>
                                <li>
                                    <form action="${pageContext.request.contextPath}/logout" method="POST" class="px-2 py-1">
                                        <button type="submit" class="btn btn-sm btn-outline-danger w-100">
                                            <i class="bi bi-box-arrow-right me-1"></i> Sign Out
                                        </button>
                                    </form>
                                </li>
                            </ul>
                        </li>
                    </c:when>
                    <c:otherwise>
                        <!-- Sign In & Register Buttons -->
                        <li class="nav-item ms-lg-2">
                            <a class="btn btn-outline-light btn-sm px-3 rounded-pill me-2" href="${pageContext.request.contextPath}/login">
                                <i class="bi bi-box-arrow-in-right me-1"></i> Sign In
                            </a>
                        </li>
                        <li class="nav-item">
                            <a class="btn btn-ss-primary btn-sm px-3 rounded-pill" href="${pageContext.request.contextPath}/register">
                                Register
                            </a>
                        </li>
                    </c:otherwise>
                </c:choose>
            </ul>
        </div>
    </div>
</nav>

<!-- System Alert Messages for Feedback / Error -->
<div class="container mt-3">
    <c:if test="${not empty param.msg}">
        <div class="alert alert-success alert-dismissible fade show shadow-sm" role="alert">
            <i class="bi bi-check-circle-fill me-2"></i>
            <c:out value="${param.msg}"/>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>
    <c:if test="${not empty param.error}">
        <div class="alert alert-danger alert-dismissible fade show shadow-sm" role="alert">
            <i class="bi bi-exclamation-triangle-fill me-2"></i>
            <c:out value="${param.error}"/>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>
</div>
