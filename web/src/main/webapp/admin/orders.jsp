<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="pageTitle" value="Order Fulfillment | Admin" scope="request"/>
<jsp:include page="/fragments/header.jsp"/>
<jsp:include page="/fragments/navbar.jsp"/>

<div class="container-fluid p-0">
    <div class="row g-0">
        <jsp:include page="/fragments/sidebar.jsp">
            <jsp:param name="activeNav" value="orders"/>
        </jsp:include>

        <div class="col-lg-9 col-md-8 p-4">
            <div class="d-flex justify-content-between align-items-center mb-4">
                <div>
                    <h3 class="fw-bold mb-1">Customer Orders</h3>
                    <p class="text-secondary small mb-0">Inspect orders, dispatch shipments, and track mock payments</p>
                </div>
            </div>

            <!-- Status Tabs Filter -->
            <div class="mb-3 d-flex gap-2">
                <a href="${pageContext.request.contextPath}/admin/orders?status=ALL" 
                   class="btn btn-sm ${empty selectedStatus || selectedStatus == 'ALL' ? 'btn-primary' : 'btn-outline-secondary'} rounded-pill">All Orders</a>
                <a href="${pageContext.request.contextPath}/admin/orders?status=PLACED" 
                   class="btn btn-sm ${selectedStatus == 'PLACED' ? 'btn-primary' : 'btn-outline-secondary'} rounded-pill">Placed (Pending)</a>
                <a href="${pageContext.request.contextPath}/admin/orders?status=CONFIRMED" 
                   class="btn btn-sm ${selectedStatus == 'CONFIRMED' ? 'btn-primary' : 'btn-outline-secondary'} rounded-pill">Confirmed</a>
                <a href="${pageContext.request.contextPath}/admin/orders?status=SHIPPED" 
                   class="btn btn-sm ${selectedStatus == 'SHIPPED' ? 'btn-primary' : 'btn-outline-secondary'} rounded-pill">Shipped</a>
                <a href="${pageContext.request.contextPath}/admin/orders?status=DELIVERED" 
                   class="btn btn-sm ${selectedStatus == 'DELIVERED' ? 'btn-primary' : 'btn-outline-secondary'} rounded-pill">Delivered</a>
                <a href="${pageContext.request.contextPath}/admin/orders?status=CANCELLED" 
                   class="btn btn-sm ${selectedStatus == 'CANCELLED' ? 'btn-primary' : 'btn-outline-secondary'} rounded-pill">Cancelled</a>
            </div>

            <!-- Orders Table -->
            <div class="card border-0 shadow-sm rounded-4 p-4 bg-white">
                <div class="table-responsive">
                    <table class="table align-middle table-hover small">
                        <thead class="text-secondary text-uppercase">
                            <tr>
                                <th>Order ID</th>
                                <th>Date</th>
                                <th>Customer</th>
                                <th>Amount</th>
                                <th>Payment</th>
                                <th>Status</th>
                                <th class="text-end">Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${orders}" var="ord">
                                <tr>
                                    <td class="fw-bold">#${ord.orderId}</td>
                                    <td><fmt:formatDate value="${ord.createdAt}" pattern="dd MMM yyyy, hh:mm a"/></td>
                                    <td>
                                        <span class="fw-semibold text-dark">${ord.customerName}</span>
                                        <div class="text-muted" style="font-size: 0.75rem;">${ord.customerEmail}</div>
                                    </td>
                                    <td class="fw-bold text-primary">₹<fmt:formatNumber value="${ord.totalAmount}" minFractionDigits="2"/></td>
                                    <td>
                                        <span class="badge ${ord.paymentStatus == 'COMPLETED' ? 'bg-success' : 'bg-warning text-dark'} rounded-pill">
                                            ${ord.paymentMethod} &bull; ${ord.paymentStatus}
                                        </span>
                                    </td>
                                    <td>
                                        <span class="badge ${ord.orderStatus == 'DELIVERED' ? 'bg-success' : (ord.orderStatus == 'CANCELLED' ? 'bg-danger' : 'bg-primary')} rounded-pill">
                                            ${ord.orderStatus}
                                        </span>
                                    </td>
                                    <td class="text-end">
                                        <a href="${pageContext.request.contextPath}/admin/orders/details?id=${ord.orderId}" class="btn btn-outline-primary btn-sm py-0 px-2">
                                            Manage Status
                                        </a>
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
