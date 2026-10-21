<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="pageTitle" value="Order #${order.orderId} Details | ShopSphere" scope="request"/>
<jsp:include page="/fragments/header.jsp"/>
<jsp:include page="/fragments/navbar.jsp"/>

<main class="py-4">
    <div class="container">
        <!-- Breadcrumb -->
        <nav aria-label="breadcrumb" class="mb-4">
            <ol class="breadcrumb">
                <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/home">Home</a></li>
                <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/orders">Orders</a></li>
                <li class="breadcrumb-item active" aria-current="page">#${order.orderId}</li>
            </ol>
        </nav>

        <div class="row g-4">
            <div class="col-lg-8">
                <!-- Status Timeline Card -->
                <div class="card border-0 shadow-sm rounded-4 p-4 mb-4">
                    <div class="d-flex justify-content-between align-items-center mb-4">
                        <h5 class="fw-bold mb-0">Delivery Status Timeline</h5>
                        <span class="badge ${order.orderStatus == 'DELIVERED' ? 'bg-success' : (order.orderStatus == 'CANCELLED' ? 'bg-danger' : 'bg-primary')} fs-6 px-3 py-2 rounded-pill">
                            ${order.orderStatus}
                        </span>
                    </div>

                    <!-- Progress Bar -->
                    <div class="position-relative m-4">
                        <div class="progress" style="height: 4px;">
                            <c:set var="prog" value="${order.orderStatus == 'PLACED' ? 25 : (order.orderStatus == 'CONFIRMED' ? 50 : (order.orderStatus == 'SHIPPED' ? 75 : (order.orderStatus == 'DELIVERED' ? 100 : 0)))}"/>
                            <div class="progress-bar ${order.orderStatus == 'CANCELLED' ? 'bg-danger w-100' : 'bg-primary'}" 
                                 role="progressbar" style="width: ${prog}%;"></div>
                        </div>
                        <div class="d-flex justify-content-between position-relative mt-2 text-secondary small">
                            <span>Placed</span>
                            <span>Confirmed</span>
                            <span>Shipped</span>
                            <span>Delivered</span>
                        </div>
                    </div>
                </div>

                <!-- Order Items Table -->
                <div class="card border-0 shadow-sm rounded-4 p-4 mb-4">
                    <h5 class="fw-bold mb-3">Purchased Items</h5>
                    <div class="table-responsive">
                        <table class="table align-middle">
                            <thead class="text-secondary small text-uppercase">
                                <tr>
                                    <th>Item</th>
                                    <th>Price</th>
                                    <th>Quantity</th>
                                    <th class="text-end">Subtotal</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${order.items}" var="oi">
                                    <tr>
                                        <td>
                                            <div class="d-flex align-items-center gap-3">
                                                <img src="${oi.product.imageUrl}" alt="${oi.product.name}" 
                                                     class="rounded-3 border" style="width: 50px; height: 50px; object-fit: contain;">
                                                <div>
                                                    <span class="fw-semibold text-dark"><c:out value="${oi.product.name}"/></span>
                                                    <div class="small text-muted">${oi.product.brand}</div>
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

                <!-- Cancellation Action -->
                <c:if test="${order.cancellable}">
                    <div class="card border-0 shadow-sm rounded-4 p-4 border-danger-subtle bg-danger-subtle">
                        <div class="d-flex justify-content-between align-items-center flex-wrap gap-3">
                            <div>
                                <h6 class="fw-bold text-danger mb-1">Need to cancel this order?</h6>
                                <p class="small text-secondary mb-0">Eligible orders in Placed or Confirmed state will automatically restore item inventory.</p>
                            </div>
                            <form action="${pageContext.request.contextPath}/orders/cancel" method="POST" 
                                  onsubmit="return confirm('Are you sure you want to cancel this order?');">
                                <input type="hidden" name="orderId" value="${order.orderId}">
                                <button type="submit" class="btn btn-outline-danger btn-sm px-3">
                                    <i class="bi bi-x-circle me-1"></i> Cancel Order
                                </button>
                            </form>
                        </div>
                    </div>
                </c:if>
            </div>

            <!-- Order Details Sidebar -->
            <div class="col-lg-4">
                <!-- Shipping Address Snapshot -->
                <div class="card border-0 shadow-sm rounded-4 p-4 mb-4">
                    <h6 class="fw-bold mb-3"><i class="bi bi-geo-alt-fill text-primary me-2"></i>Delivery Address</h6>
                    <c:choose>
                        <c:when test="${not empty order.address}">
                            <div class="small text-secondary">
                                <span class="badge bg-secondary mb-2">${order.address.addressType}</span>
                                <div class="fw-semibold text-dark mb-1">${order.customerName}</div>
                                <div>${order.address.addressLine}</div>
                                <div>${order.address.city}, ${order.address.state} - ${order.address.pincode}</div>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <span class="text-muted small">Standard address on record</span>
                        </c:otherwise>
                    </c:choose>
                </div>

                <!-- Payment Summary -->
                <div class="card border-0 shadow-sm rounded-4 p-4">
                    <h6 class="fw-bold mb-3"><i class="bi bi-receipt text-primary me-2"></i>Invoice Breakdown</h6>
                    <div class="d-flex justify-content-between mb-2 small text-secondary">
                        <span>Payment Method</span>
                        <span class="fw-bold text-dark">${order.paymentMethod}</span>
                    </div>
                    <div class="d-flex justify-content-between mb-2 small text-secondary">
                        <span>Payment Status</span>
                        <span class="badge bg-success">${order.paymentStatus}</span>
                    </div>
                    <c:if test="${order.discount > 0}">
                        <div class="d-flex justify-content-between mb-2 small text-secondary">
                            <span>Discount Applied</span>
                            <span class="text-danger fw-bold">- ₹<fmt:formatNumber value="${order.discount}" minFractionDigits="2"/></span>
                        </div>
                    </c:if>
                    <hr>
                    <div class="d-flex justify-content-between align-items-baseline">
                        <span class="fw-bold">Total Invoiced</span>
                        <span class="fs-4 fw-bolder text-primary">₹<fmt:formatNumber value="${order.totalAmount}" minFractionDigits="2"/></span>
                    </div>
                </div>
            </div>
        </div>
    </div>
</main>

<jsp:include page="/fragments/footer.jsp"/>
