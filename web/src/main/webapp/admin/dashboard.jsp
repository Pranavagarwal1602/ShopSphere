<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="pageTitle" value="Admin Dashboard | ShopSphere" scope="request"/>
<jsp:include page="/fragments/header.jsp"/>
<jsp:include page="/fragments/navbar.jsp"/>

<div class="container-fluid p-0">
    <div class="row g-0">
        <!-- Reusable Admin Sidebar -->
        <jsp:include page="/fragments/sidebar.jsp">
            <jsp:param name="activeNav" value="dashboard"/>
        </jsp:include>

        <!-- Main Dashboard View -->
        <div class="col-lg-9 col-md-8 p-4">
            <div class="d-flex justify-content-between align-items-center mb-4">
                <div>
                    <h3 class="fw-bold mb-1">Administrative Overview</h3>
                    <p class="text-secondary small mb-0">Live MySQL metrics, order status tracking, and inventory monitors</p>
                </div>
                <span class="badge bg-success-subtle text-success border border-success-subtle px-3 py-2 rounded-pill">
                    <i class="bi bi-circle-fill me-1 small"></i> System Active
                </span>
            </div>

            <!-- KPI Metric Cards Grid -->
            <div class="row g-3 mb-4">
                <div class="col-xl-3 col-sm-6">
                    <div class="card border-0 shadow-sm rounded-4 p-3 bg-white">
                        <div class="d-flex justify-content-between align-items-center mb-2">
                            <span class="text-secondary small fw-bold text-uppercase">Total Sales</span>
                            <div class="bg-primary-subtle text-primary p-2 rounded-3"><i class="bi bi-currency-rupee fs-5"></i></div>
                        </div>
                        <h3 class="fw-bolder mb-0 text-dark">₹<fmt:formatNumber value="${totalRevenue}" minFractionDigits="2"/></h3>
                        <small class="text-success fw-semibold"><i class="bi bi-graph-up-arrow me-1"></i>Excludes cancelled</small>
                    </div>
                </div>

                <div class="col-xl-3 col-sm-6">
                    <div class="card border-0 shadow-sm rounded-4 p-3 bg-white">
                        <div class="d-flex justify-content-between align-items-center mb-2">
                            <span class="text-secondary small fw-bold text-uppercase">Total Orders</span>
                            <div class="bg-info-subtle text-info p-2 rounded-3"><i class="bi bi-bag-check fs-5"></i></div>
                        </div>
                        <h3 class="fw-bolder mb-0 text-dark">${totalOrders}</h3>
                        <small class="text-warning fw-semibold">${pendingOrders} Pending Fulfillment</small>
                    </div>
                </div>

                <div class="col-xl-3 col-sm-6">
                    <div class="card border-0 shadow-sm rounded-4 p-3 bg-white">
                        <div class="d-flex justify-content-between align-items-center mb-2">
                            <span class="text-secondary small fw-bold text-uppercase">Active Customers</span>
                            <div class="bg-success-subtle text-success p-2 rounded-3"><i class="bi bi-people fs-5"></i></div>
                        </div>
                        <h3 class="fw-bolder mb-0 text-dark">${totalCustomers}</h3>
                        <small class="text-secondary">Registered accounts</small>
                    </div>
                </div>

                <div class="col-xl-3 col-sm-6">
                    <div class="card border-0 shadow-sm rounded-4 p-3 bg-white">
                        <div class="d-flex justify-content-between align-items-center mb-2">
                            <span class="text-secondary small fw-bold text-uppercase">Total Products</span>
                            <div class="bg-warning-subtle text-warning p-2 rounded-3"><i class="bi bi-box-seam fs-5"></i></div>
                        </div>
                        <h3 class="fw-bolder mb-0 text-dark">${totalProducts}</h3>
                        <small class="text-danger fw-semibold">${lowStockItems.size()} Low-Stock Items</small>
                    </div>
                </div>
            </div>

            <!-- Recent Orders and Low Stock Tables -->
            <div class="row g-4">
                <!-- Recent Orders -->
                <div class="col-lg-7">
                    <div class="card border-0 shadow-sm rounded-4 p-4 bg-white h-100">
                        <div class="d-flex justify-content-between align-items-center mb-3">
                            <h5 class="fw-bold mb-0">Recent Customer Orders</h5>
                            <a href="${pageContext.request.contextPath}/admin/orders" class="small text-decoration-none fw-semibold">View All</a>
                        </div>
                        <div class="table-responsive">
                            <table class="table align-middle table-hover small">
                                <thead class="text-secondary text-uppercase">
                                    <tr>
                                        <th>Order</th>
                                        <th>Customer</th>
                                        <th>Total</th>
                                        <th>Status</th>
                                        <th>Action</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach items="${recentOrders}" var="o">
                                        <tr>
                                            <td class="fw-bold">#${o.orderId}</td>
                                            <td>${o.customerName}</td>
                                            <td>₹<fmt:formatNumber value="${o.totalAmount}" minFractionDigits="2"/></td>
                                            <td>
                                                <span class="badge ${o.orderStatus == 'DELIVERED' ? 'bg-success' : (o.orderStatus == 'CANCELLED' ? 'bg-danger' : 'bg-primary')} rounded-pill">
                                                    ${o.orderStatus}
                                                </span>
                                            </td>
                                            <td>
                                                <a href="${pageContext.request.contextPath}/admin/orders/details?id=${o.orderId}" class="btn btn-sm btn-outline-primary py-0 px-2">
                                                    Inspect
                                                </a>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>

                <!-- Low Stock Alerts -->
                <div class="col-lg-5">
                    <div class="card border-0 shadow-sm rounded-4 p-4 bg-white h-100">
                        <div class="d-flex justify-content-between align-items-center mb-3">
                            <h5 class="fw-bold mb-0"><i class="bi bi-exclamation-triangle-fill text-warning me-2"></i>Low Stock Inventory</h5>
                            <a href="${pageContext.request.contextPath}/admin/inventory" class="small text-decoration-none fw-semibold">Manage</a>
                        </div>
                        <c:choose>
                            <c:when test="${not empty lowStockItems}">
                                <ul class="list-group list-group-flush small">
                                    <c:forEach items="${lowStockItems}" var="lp">
                                        <li class="list-group-item d-flex justify-content-between align-items-center px-0">
                                            <div>
                                                <span class="fw-semibold text-dark d-block text-truncate" style="max-width: 220px;">
                                                    <c:out value="${lp.name}"/>
                                                </span>
                                                <span class="text-secondary">${lp.categoryName}</span>
                                            </div>
                                            <span class="badge bg-danger-subtle text-danger border border-danger-subtle rounded-pill fs-7">
                                                ${lp.stock} left
                                            </span>
                                        </li>
                                    </c:forEach>
                                </ul>
                            </c:when>
                            <c:otherwise>
                                <p class="text-secondary small mb-0">All product inventories are well stocked!</p>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/fragments/footer.jsp"/>
