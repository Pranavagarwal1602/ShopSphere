<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="pageTitle" value="Stock & Inventory | Admin" scope="request"/>
<jsp:include page="/fragments/header.jsp"/>
<jsp:include page="/fragments/navbar.jsp"/>

<div class="container-fluid p-0">
    <div class="row g-0">
        <jsp:include page="/fragments/sidebar.jsp">
            <jsp:param name="activeNav" value="inventory"/>
        </jsp:include>

        <div class="col-lg-9 col-md-8 p-4">
            <div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-2">
                <div>
                    <h3 class="fw-bold mb-1">Inventory &amp; Stock Control</h3>
                    <p class="text-secondary small mb-0">Fast inline stock updates synchronized across Web, Swing, Sockets, and RMI</p>
                </div>
                <div class="d-flex gap-2">
                    <a href="${pageContext.request.contextPath}/admin/inventory" class="btn btn-sm ${empty selectedFilter ? 'btn-primary' : 'btn-outline-secondary'} rounded-pill">
                        All Items
                    </a>
                    <a href="${pageContext.request.contextPath}/admin/inventory?filter=low" class="btn btn-sm ${selectedFilter == 'low' ? 'btn-danger' : 'btn-outline-danger'} rounded-pill">
                        <i class="bi bi-exclamation-triangle-fill me-1"></i> Low Stock Only (&le; 20)
                    </a>
                </div>
            </div>

            <!-- Inventory Table -->
            <div class="card border-0 shadow-sm rounded-4 p-4 bg-white">
                <div class="table-responsive">
                    <table class="table align-middle table-hover small">
                        <thead class="text-secondary text-uppercase">
                            <tr>
                                <th>Product ID</th>
                                <th>Product Name</th>
                                <th>Category</th>
                                <th>Selling Price</th>
                                <th>Current Stock</th>
                                <th>Quick Replenishment</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${products}" var="p">
                                <tr>
                                    <td class="fw-bold">${p.productId}</td>
                                    <td>
                                        <div class="d-flex align-items-center gap-2">
                                            <img src="${p.imageUrl}" alt="${p.name}" class="rounded border" style="width: 35px; height: 35px; object-fit: contain;">
                                            <span class="fw-semibold text-dark"><c:out value="${p.name}"/></span>
                                        </div>
                                    </td>
                                    <td>${p.categoryName}</td>
                                    <td class="fw-bold">₹<fmt:formatNumber value="${p.effectivePrice}" minFractionDigits="2"/></td>
                                    <td>
                                        <span class="badge ${p.stock <= 10 ? 'bg-danger' : (p.stock <= 20 ? 'bg-warning text-dark' : 'bg-success')} rounded-pill fs-7">
                                            ${p.stock} units
                                        </span>
                                    </td>
                                    <td>
                                        <form action="${pageContext.request.contextPath}/admin/inventory/update-stock" method="POST" class="d-flex align-items-center gap-2">
                                            <input type="hidden" name="productId" value="${p.productId}">
                                            <input type="number" name="stock" class="form-control form-control-sm text-center" 
                                                   value="${p.stock}" min="0" style="width: 90px;" required>
                                            <button type="submit" class="btn btn-sm btn-outline-primary py-1 px-3">
                                                Update Stock
                                            </button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/fragments/footer.jsp"/>
