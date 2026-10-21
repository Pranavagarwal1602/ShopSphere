<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="pageTitle" value="Order Confirmed | ShopSphere" scope="request"/>
<jsp:include page="/fragments/header.jsp"/>
<jsp:include page="/fragments/navbar.jsp"/>

<main class="py-5">
    <div class="container">
        <div class="row justify-content-center">
            <div class="col-lg-7 text-center">
                <div class="card border-0 shadow-lg rounded-4 p-5">
                    <div class="bg-success-subtle text-success p-4 rounded-circle d-inline-block mx-auto mb-3 fs-1">
                        <i class="bi bi-check-circle-fill"></i>
                    </div>
                    <h2 class="fw-bold mb-2">Thank You For Your Order!</h2>
                    <p class="text-secondary mb-4">
                        Your order <strong>#${order.orderId}</strong> has been successfully placed in our database. 
                        A confirmation notification has been dispatched to <strong>${order.customerEmail}</strong>.
                    </p>

                    <!-- Summary Card -->
                    <div class="p-3 bg-light rounded-3 text-start mb-4">
                        <div class="d-flex justify-content-between mb-2">
                            <span class="text-secondary">Order ID:</span>
                            <span class="fw-bold">#${order.orderId}</span>
                        </div>
                        <div class="d-flex justify-content-between mb-2">
                            <span class="text-secondary">Payment Method:</span>
                            <span class="fw-bold">${order.paymentMethod}</span>
                        </div>
                        <div class="d-flex justify-content-between mb-2">
                            <span class="text-secondary">Payment Status:</span>
                            <span class="badge bg-success">${order.paymentStatus}</span>
                        </div>
                        <div class="d-flex justify-content-between">
                            <span class="text-secondary">Total Amount Paid:</span>
                            <span class="fw-bolder text-primary">₹<fmt:formatNumber value="${order.totalAmount}" minFractionDigits="2"/></span>
                        </div>
                    </div>

                    <div class="d-flex justify-content-center gap-3 flex-wrap">
                        <a href="${pageContext.request.contextPath}/order-details?id=${order.orderId}" class="btn btn-ss-primary px-4 py-2">
                            <i class="bi bi-truck me-1"></i> Track Order Details
                        </a>
                        <a href="${pageContext.request.contextPath}/products" class="btn btn-outline-secondary px-4 py-2">
                            Continue Shopping
                        </a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</main>

<jsp:include page="/fragments/footer.jsp"/>
