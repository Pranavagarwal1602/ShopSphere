<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<div class="col-lg-3 col-md-4 p-0">
    <div class="ss-admin-sidebar p-3">
        <div class="d-flex align-items-center gap-2 px-3 py-3 mb-3 border-bottom border-secondary">
            <i class="bi bi-shield-shaded fs-4 text-primary"></i>
            <div>
                <h6 class="mb-0 text-white fw-bold">Admin Portal</h6>
                <small class="text-secondary">ShopSphere System</small>
            </div>
        </div>

        <ul class="nav flex-column">
            <li class="nav-item">
                <a class="nav-link ${param.activeNav == 'dashboard' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/dashboard">
                    <i class="bi bi-speedometer2"></i> Dashboard
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link ${param.activeNav == 'products' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/products">
                    <i class="bi bi-box-seam"></i> Products CRUD
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link ${param.activeNav == 'categories' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/categories">
                    <i class="bi bi-tags"></i> Categories
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link ${param.activeNav == 'inventory' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/inventory">
                    <i class="bi bi-layers"></i> Stock &amp; Inventory
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link ${param.activeNav == 'orders' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/orders">
                    <i class="bi bi-receipt"></i> Orders &amp; Fulfillment
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link ${param.activeNav == 'coupons' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/coupons">
                    <i class="bi bi-ticket-perforated"></i> Coupons
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link ${param.activeNav == 'users' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/users">
                    <i class="bi bi-people"></i> Customers
                </a>
            </li>
            <li class="nav-item mt-4 pt-3 border-top border-secondary">
                <a class="nav-link text-warning" href="${pageContext.request.contextPath}/home" target="_blank">
                    <i class="bi bi-box-arrow-up-right"></i> View Live Store
                </a>
            </li>
            <li class="nav-item">
                <form action="${pageContext.request.contextPath}/logout" method="POST" class="mt-2 px-2">
                    <button type="submit" class="btn btn-sm btn-outline-danger w-100">
                        <i class="bi bi-power me-1"></i> Sign Out
                    </button>
                </form>
            </li>
        </ul>
    </div>
</div>
