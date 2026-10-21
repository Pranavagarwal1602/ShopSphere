<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="pageTitle" value="Shopping Cart | ShopSphere" scope="request"/>
<jsp:include page="/fragments/header.jsp"/>
<jsp:include page="/fragments/navbar.jsp"/>

<main class="py-4">
    <div class="container">
        <h2 class="fw-bold mb-4"><i class="bi bi-cart3 text-primary me-2"></i>Shopping Cart</h2>

        <c:choose>
            <c:when test="${not empty cartItems}">
                <div class="row g-4">
                    <!-- Cart Items Table / List -->
                    <div class="col-lg-8">
                        <div class="card border-0 shadow-sm rounded-4 p-4 mb-3">
                            <div class="table-responsive">
                                <table class="table align-middle mb-0">
                                    <thead class="text-secondary small text-uppercase">
                                        <tr>
                                            <th>Product</th>
                                            <th>Price</th>
                                            <th style="width: 140px;">Quantity</th>
                                            <th>Total</th>
                                            <th>Action</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach items="${cartItems}" var="item">
                                            <tr>
                                                <td>
                                                    <div class="d-flex align-items-center gap-3">
                                                        <img src="${item.product.imageUrl}" alt="${item.product.name}" 
                                                             class="rounded-3 border" style="width: 65px; height: 65px; object-fit: contain;">
                                                        <div>
                                                            <a href="${pageContext.request.contextPath}/product-details?id=${item.productId}" class="fw-semibold text-dark text-decoration-none">
                                                                <c:out value="${item.product.name}"/>
                                                            </a>
                                                            <div class="small text-muted">${item.product.brand}</div>
                                                        </div>
                                                    </div>
                                                </td>
                                                <td>
                                                    <span class="fw-bold">₹<fmt:formatNumber value="${item.product.effectivePrice}" minFractionDigits="2"/></span>
                                                </td>
                                                <td>
                                                    <form action="${pageContext.request.contextPath}/cart/update" method="POST" class="d-flex align-items-center gap-2">
                                                        <input type="hidden" name="cartId" value="${item.cartId}">
                                                        <input type="number" name="quantity" class="form-control form-control-sm text-center" 
                                                               value="${item.quantity}" min="1" max="${item.product.stock}">
                                                        <button type="submit" class="btn btn-sm btn-outline-primary" title="Update">
                                                            <i class="bi bi-arrow-clockwise"></i>
                                                        </button>
                                                    </form>
                                                </td>
                                                <td>
                                                    <span class="fw-bold text-primary">₹<fmt:formatNumber value="${item.itemTotal}" minFractionDigits="2"/></span>
                                                </td>
                                                <td>
                                                    <form action="${pageContext.request.contextPath}/cart/remove" method="POST">
                                                        <input type="hidden" name="cartId" value="${item.cartId}">
                                                        <button type="submit" class="btn btn-sm text-danger" title="Remove item">
                                                            <i class="bi bi-trash3 fs-5"></i>
                                                        </button>
                                                    </form>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>

                            <div class="d-flex justify-content-between align-items-center mt-4 pt-3 border-top">
                                <a href="${pageContext.request.contextPath}/products" class="btn btn-outline-secondary btn-sm">
                                    <i class="bi bi-arrow-left me-1"></i> Continue Shopping
                                </a>
                                <form action="${pageContext.request.contextPath}/cart/clear" method="POST">
                                    <button type="submit" class="btn btn-outline-danger btn-sm">
                                        <i class="bi bi-x-circle me-1"></i> Clear Cart
                                    </button>
                                </form>
                            </div>
                        </div>
                    </div>

                    <!-- Cart Order Summary Sidebar -->
                    <div class="col-lg-4">
                        <div class="card border-0 shadow-sm rounded-4 p-4 sticky-top" style="top: 85px;">
                            <h5 class="fw-bold mb-3">Order Summary</h5>
                            <div class="d-flex justify-content-between mb-2">
                                <span class="text-secondary">Total Items</span>
                                <span class="fw-bold">${totalQuantity}</span>
                            </div>
                            <div class="d-flex justify-content-between mb-3">
                                <span class="text-secondary">Subtotal</span>
                                <span class="fw-bold text-dark">₹<fmt:formatNumber value="${subtotal}" minFractionDigits="2"/></span>
                            </div>
                            <div class="d-flex justify-content-between mb-3">
                                <span class="text-secondary">Shipping Estimate</span>
                                <span class="text-success fw-bold">FREE</span>
                            </div>
                            <hr>
                            <div class="d-flex justify-content-between align-items-baseline mb-4">
                                <span class="fs-5 fw-bold">Estimated Total</span>
                                <span class="fs-4 fw-bolder text-primary">₹<fmt:formatNumber value="${subtotal}" minFractionDigits="2"/></span>
                            </div>

                            <a href="${pageContext.request.contextPath}/checkout" class="btn btn-ss-primary w-100 py-2 fw-bold">
                                Proceed to Checkout <i class="bi bi-arrow-right ms-1"></i>
                            </a>
                        </div>
                    </div>
                </div>
            </c:when>
            <c:otherwise>
                <!-- Empty Cart State -->
                <div class="card border-0 shadow-sm rounded-4 p-5 text-center my-4">
                    <div class="bg-light p-4 rounded-circle d-inline-block mx-auto mb-3 text-secondary fs-1">
                        <i class="bi bi-cart-x"></i>
                    </div>
                    <h4 class="fw-bold">Your Shopping Cart is Empty</h4>
                    <p class="text-secondary mb-4">Explore our wide selection of tech gadgets, apparel, and books to fill it up.</p>
                    <div>
                        <a href="${pageContext.request.contextPath}/products" class="btn btn-ss-primary px-4 py-2">
                            <i class="bi bi-bag-plus me-1"></i> Start Shopping
                        </a>
                    </div>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</main>

<jsp:include page="/fragments/footer.jsp"/>
