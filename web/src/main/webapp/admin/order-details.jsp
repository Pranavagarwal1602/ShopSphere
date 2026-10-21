<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="pageTitle" value="Order #${order.orderId} Details | Admin" scope="request"/>
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
                    <h3 class="fw-bold mb-1">Order #${order.orderId} Details</h3>
                    <p class="text-secondary small mb-0">Customer: ${order.customerName} (${order.customerEmail})</p>
                </div>
                <a href="${pageContext.request.contextPath}/admin/orders" class="btn btn-outline-secondary btn-sm">
                    <i class="bi bi-arrow-left me-1"></i> Back to Orders
                </a>
            </div>

            <div class="row g-4">
                <div class="col-lg-8">
                    <!-- Update Status Card -->
                    <div class="card border-0 shadow-sm rounded-4 p-4 mb-4 bg-white">
                        <h5 class="fw-bold mb-3">Update Order &amp; Payment Status</h5>
                        <form action="${pageContext.request.contextPath}/admin/orders/update-status" method="POST" class="row g-3">
                            <input type="hidden" name="orderId" value="${order.orderId}">

                            <div class="col-md-6">
                                <label class="form-label small fw-semibold">Order Fulfillment Status</label>
                                <select name="orderStatus" class="form-select">
                                    <option value="PLACED" ${order.orderStatus == 'PLACED' ? 'selected' : ''}>PLACED</option>
                                    <option value="CONFIRMED" ${order.orderStatus == 'CONFIRMED' ? 'selected' : ''}>CONFIRMED</option>
                                    <option value="SHIPPED" ${order.orderStatus == 'SHIPPED' ? 'selected' : ''}>SHIPPED</option>
                                    <option value="DELIVERED" ${order.orderStatus == 'DELIVERED' ? 'selected' : ''}>DELIVERED</option>
                                    <option value="CANCELLED" ${order.orderStatus == 'CANCELLED' ? 'selected' : ''}>CANCELLED</option>
                                </select>
                            </div>

                            <div class="col-md-6">
                                <label class="form-label small fw-semibold">Payment Status</label>
                                <select name="paymentStatus" class="form-select">
                                    <option value="PENDING" ${order.paymentStatus == 'PENDING' ? 'selected' : ''}>PENDING</option>
                                    <option value="COMPLETED" ${order.paymentStatus == 'COMPLETED' ? 'selected' : ''}>COMPLETED</option>
                                    <option value="FAILED" ${order.paymentStatus == 'FAILED' ? 'selected' : ''}>FAILED</option>
                                    <option value="REFUNDED" ${order.paymentStatus == 'REFUNDED' ? 'selected' : ''}>REFUNDED</option>
                                </select>
                            </div>

                            <div class="col-12 text-end">
                                <button type="submit" class="btn btn-ss-primary btn-sm px-4">Save Changes</button>
                            </div>
                        </form>
                    </div>

                    <!-- Items Table -->
                    <div class="card border-0 shadow-sm rounded-4 p-4 bg-white">
                        <h5 class="fw-bold mb-3">Order Items (${order.items.size()})</h5>
                        <div class="table-responsive">
                            <table class="table align-middle small">
                                <thead>
                                    <tr>
                                        <th>Product</th>
                                        <th>Unit Price</th>
                                        <th>Qty</th>
                                        <th class="text-end">Subtotal</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach items="${order.items}" var="oi">
                                        <tr>
                                            <td>
                                                <div class="d-flex align-items-center gap-2">
                                                    <img src="${oi.product.imageUrl}" class="rounded border" style="width: 40px; height: 40px; object-fit: contain;">
                                                    <div>
                                                        <span class="fw-semibold text-dark">${oi.product.name}</span>
                                                        <div class="text-muted" style="font-size: 0.75rem;">ID: ${oi.productId}</div>
                                                    </div>
                                                </div>
                                            </td>
                                            <td>₹<fmt:formatNumber value="${oi.price}" minFractionDigits="2"/></td>
                                            <td>${oi.quantity}</td>
                                            <td class="text-end fw-bold">₹<fmt:formatNumber value="${oi.subtotal}" minFractionDigits="2"/></td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>

                <!-- Summary Sidebar -->
                <div class="col-lg-4">
                    <div class="card border-0 shadow-sm rounded-4 p-4 bg-white mb-4">
                        <h6 class="fw-bold mb-3">Customer Information</h6>
                        <div class="small text-secondary mb-1"><strong>Name:</strong> ${order.customerName}</div>
                        <div class="small text-secondary mb-3"><strong>Email:</strong> ${order.customerEmail}</div>

                        <h6 class="fw-bold mb-2">Delivery Address</h6>
                        <div class="small text-secondary">
                            ${order.address.addressLine}<br>
                            ${order.address.city}, ${order.address.state} - ${order.address.pincode}
                        </div>
                    </div>

                    <div class="card border-0 shadow-sm rounded-4 p-4 bg-white">
                        <h6 class="fw-bold mb-3">Financial Breakdown</h6>
                        <div class="d-flex justify-content-between mb-2 small text-secondary">
                            <span>Payment Method</span>
                            <span class="fw-bold">${order.paymentMethod}</span>
                        </div>
                        <div class="d-flex justify-content-between mb-2 small text-secondary">
                            <span>Discount</span>
                            <span class="text-danger fw-bold">- ₹<fmt:formatNumber value="${order.discount}" minFractionDigits="2"/></span>
                        </div>
                        <hr>
                        <div class="d-flex justify-content-between align-items-baseline">
                            <span class="fw-bold">Total Invoiced</span>
                            <span class="fs-4 fw-bolder text-primary">₹<fmt:formatNumber value="${order.totalAmount}" minFractionDigits="2"/></span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/fragments/footer.jsp"/>
