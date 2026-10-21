<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="pageTitle" value="My Wishlist | ShopSphere" scope="request"/>
<jsp:include page="/fragments/header.jsp"/>
<jsp:include page="/fragments/navbar.jsp"/>

<main class="py-4">
    <div class="container">
        <h2 class="fw-bold mb-4"><i class="bi bi-heart text-danger me-2"></i>My Saved Wishlist</h2>

        <c:choose>
            <c:when test="${not empty wishlistItems}">
                <div class="row g-4">
                    <c:forEach items="${wishlistItems}" var="wi">
                        <div class="col-lg-3 col-md-4 col-sm-6">
                            <div class="ss-card">
                                <div class="ss-card-img-wrap">
                                    <a href="${pageContext.request.contextPath}/product-details?id=${wi.productId}">
                                        <img src="${wi.product.imageUrl}" class="ss-card-img" alt="${wi.product.name}">
                                    </a>
                                </div>
                                <div class="ss-card-body">
                                    <span class="ss-brand-subtitle">${wi.product.brand}</span>
                                    <a href="${pageContext.request.contextPath}/product-details?id=${wi.productId}" class="ss-product-title">
                                        <c:out value="${wi.product.name}"/>
                                    </a>
                                    <div class="ss-price-tag mb-3">
                                        ₹<fmt:formatNumber value="${wi.product.effectivePrice}" minFractionDigits="2"/>
                                    </div>
                                    <div class="d-flex gap-2 mt-auto">
                                        <form action="${pageContext.request.contextPath}/wishlist/move-to-cart" method="POST" class="flex-grow-1">
                                            <input type="hidden" name="wishlistId" value="${wi.wishlistId}">
                                            <input type="hidden" name="productId" value="${wi.productId}">
                                            <button type="submit" class="btn btn-ss-primary btn-sm w-100">
                                                <i class="bi bi-cart-plus me-1"></i> Move to Cart
                                            </button>
                                        </form>
                                        <form action="${pageContext.request.contextPath}/wishlist/remove" method="POST">
                                            <input type="hidden" name="wishlistId" value="${wi.wishlistId}">
                                            <button type="submit" class="btn btn-outline-danger btn-sm" title="Remove">
                                                <i class="bi bi-trash"></i>
                                            </button>
                                        </form>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </c:when>
            <c:otherwise>
                <div class="card border-0 shadow-sm rounded-4 p-5 text-center my-4">
                    <i class="bi bi-heart fs-1 text-secondary mb-3"></i>
                    <h4 class="fw-bold">Your Wishlist is Empty</h4>
                    <p class="text-secondary mb-4">Tap the heart icon on any product to save it here for later.</p>
                    <div>
                        <a href="${pageContext.request.contextPath}/products" class="btn btn-ss-primary px-4 py-2">
                            Explore Products
                        </a>
                    </div>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</main>

<jsp:include page="/fragments/footer.jsp"/>
