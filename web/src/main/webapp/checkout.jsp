<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="pageTitle" value="Checkout | ShopSphere" scope="request"/>
<jsp:include page="/fragments/header.jsp"/>
<jsp:include page="/fragments/navbar.jsp"/>

<main class="py-4">
    <div class="container">
        <h2 class="fw-bold mb-4"><i class="bi bi-credit-card-2-front text-primary me-2"></i>Secure Checkout</h2>

        <form action="${pageContext.request.contextPath}/checkout/place-order" method="POST">
            <input type="hidden" name="csrfToken" value="${csrfToken}">
            <input type="hidden" name="couponCode" value="${appliedCouponCode}">

            <div class="row g-4">
                <!-- Main Flow: Address & Payment Selection -->
                <div class="col-lg-8">
                    <!-- Step 1: Select Delivery Address -->
                    <div class="card border-0 shadow-sm rounded-4 p-4 mb-4">
                        <div class="d-flex justify-content-between align-items-center mb-3">
                            <h5 class="fw-bold mb-0">1. Select Delivery Address</h5>
                            <a href="${pageContext.request.contextPath}/profile" class="btn btn-outline-primary btn-sm rounded-pill">
                                <i class="bi bi-plus-lg me-1"></i> Add / Manage Address
                            </a>
                        </div>

                        <c:choose>
                            <c:when test="${not empty addresses}">
                                <div class="row g-3">
                                    <c:forEach items="${addresses}" var="addr" varStatus="loop">
                                        <div class="col-md-6">
                                            <div class="card p-3 border rounded-3 h-100 ${addr.default ? 'border-primary bg-primary-subtle' : ''}">
                                                <div class="form-check">
                                                    <input class="form-check-input" type="radio" name="addressId" 
                                                           id="addr_${addr.addressId}" value="${addr.addressId}" 
                                                           ${addr.default || loop.first ? 'checked' : ''} required>
                                                    <label class="form-check-label w-100" for="addr_${addr.addressId}">
                                                        <span class="badge bg-secondary mb-1">${addr.addressType}</span>
                                                        <div class="fw-semibold text-dark">${addr.addressLine}</div>
                                                        <div class="small text-secondary">${addr.city}, ${addr.state} - ${addr.pincode}</div>
                                                    </label>
                                                </div>
                                            </div>
                                        </div>
                                    </c:forEach>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="alert alert-warning mb-0">
                                    <i class="bi bi-exclamation-triangle-fill me-2"></i>
                                    No shipping address found. Please <a href="${pageContext.request.contextPath}/profile" class="fw-bold">add an address</a> before confirming checkout.
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <!-- Step 2: Payment Method Selection -->
                    <div class="card border-0 shadow-sm rounded-4 p-4 mb-4">
                        <h5 class="fw-bold mb-3">2. Select Payment Method</h5>
                        <div class="form-check p-3 border rounded-3 mb-3">
                            <input class="form-check-input ms-0 me-3" type="radio" name="paymentMethod" id="payOnline" value="ONLINE_MOCK" checked>
                            <label class="form-check-label" for="payOnline">
                                <span class="fw-bold d-block"><i class="bi bi-credit-card text-primary me-2"></i>Simulated Online Payment (Instant Confirmation)</span>
                                <small class="text-secondary">Simulates UPI / Credit Card transaction. No real payment gateway or card storage.</small>
                            </label>
                        </div>
                        <div class="form-check p-3 border rounded-3">
                            <input class="form-check-input ms-0 me-3" type="radio" name="paymentMethod" id="payCod" value="COD">
                            <label class="form-check-label" for="payCod">
                                <span class="fw-bold d-block"><i class="bi bi-cash-stack text-success me-2"></i>Cash on Delivery (Pay upon arrival)</span>
                                <small class="text-secondary">Pay securely via cash or mobile QR scanner at your doorstep.</small>
                            </label>
                        </div>
                    </div>
                </div>

                <!-- Step 3: Order Review & Coupon Application Sidebar -->
                <div class="col-lg-4">
                    <div class="card border-0 shadow-sm rounded-4 p-4 sticky-top" style="top: 85px;">
                        <h5 class="fw-bold mb-3">Review &amp; Totals</h5>

                        <!-- Coupon Form -->
                        <div class="mb-3">
                            <label class="form-label small fw-semibold text-secondary">Apply Promo Coupon</label>
                            <div class="input-group">
                                <input type="text" id="couponInput" class="form-control form-control-sm text-uppercase" 
                                       placeholder="PROMO CODE" value="${appliedCouponCode}">
                                <button type="button" class="btn btn-outline-primary btn-sm" onclick="applyCouponCode()">
                                    Apply
                                </button>
                            </div>
                            <c:if test="${not empty couponMsg}">
                                <div class="small text-success mt-1 fw-bold"><i class="bi bi-check-circle me-1"></i>${couponMsg}</div>
                            </c:if>
                            <c:if test="${not empty couponError}">
                                <div class="small text-danger mt-1"><i class="bi bi-exclamation-circle me-1"></i>${couponError}</div>
                            </c:if>
                        </div>

                        <!-- Price Breakdown -->
                        <div class="d-flex justify-content-between mb-2">
                            <span class="text-secondary">Items Subtotal</span>
                            <span class="fw-bold">₹<fmt:formatNumber value="${subtotal}" minFractionDigits="2"/></span>
                        </div>
                        <div class="d-flex justify-content-between mb-2">
                            <span class="text-secondary">Coupon Discount</span>
                            <span class="text-danger fw-bold">- ₹<fmt:formatNumber value="${discount}" minFractionDigits="2"/></span>
                        </div>
                        <div class="d-flex justify-content-between mb-3">
                            <span class="text-secondary">Delivery Fee</span>
                            <span class="text-success fw-bold">FREE</span>
                        </div>
                        <hr>
                        <div class="d-flex justify-content-between align-items-baseline mb-4">
                            <span class="fs-5 fw-bold">Grand Total</span>
                            <span class="fs-4 fw-bolder text-primary">₹<fmt:formatNumber value="${finalTotal}" minFractionDigits="2"/></span>
                        </div>

                        <!-- Place Order Button -->
                        <button type="submit" class="btn btn-ss-primary w-100 py-3 fw-bold" ${empty addresses ? 'disabled' : ''}>
                            <i class="bi bi-shield-check me-2"></i> Confirm &amp; Place Order
                        </button>
                    </div>
                </div>
            </div>
        </form>
    </div>
</main>

<script>
function applyCouponCode() {
    const code = document.getElementById('couponInput').value.trim();
    if (code) {
        window.location.href = '${pageContext.request.contextPath}/checkout?coupon=' + encodeURIComponent(code);
    }
}
</script>

<jsp:include page="/fragments/footer.jsp"/>
