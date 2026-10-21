<%@ tag description="Product Card Custom Tag" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ attribute name="product" type="com.shopsphere.model.Product" required="true" %>

<div class="col-xl-3 col-lg-4 col-md-6 col-sm-6 mb-4">
    <div class="ss-card">
        <!-- Image & Discount Badge -->
        <div class="ss-card-img-wrap">
            <c:if test="${product.discount > 0}">
                <span class="ss-badge-discount">SAVE ₹<fmt:formatNumber value="${product.discount}" minFractionDigits="0"/></span>
            </c:if>

            <a href="${pageContext.request.contextPath}/product-details?id=${product.productId}">
                <img src="${not empty product.imageUrl ? product.imageUrl : 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=500'}" 
                     class="ss-card-img" alt="${product.name}" 
                     onerror="this.src='https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=500';">
            </a>

            <!-- Wishlist Button -->
            <form action="${pageContext.request.contextPath}/wishlist/add" method="POST" class="d-inline">
                <input type="hidden" name="productId" value="${product.productId}">
                <button type="submit" class="ss-badge-wishlist" title="Save to Wishlist">
                    <i class="bi bi-heart"></i>
                </button>
            </form>
        </div>

        <!-- Body Details -->
        <div class="ss-card-body">
            <span class="ss-brand-subtitle">${product.brand}</span>
            <a href="${pageContext.request.contextPath}/product-details?id=${product.productId}" class="ss-product-title" title="${product.name}">
                <c:out value="${product.name}"/>
            </a>

            <!-- Rating Stars -->
            <div class="d-flex align-items-center gap-1 mb-2">
                <span class="ss-stars">
                    <c:forEach begin="1" end="5" var="i">
                        <i class="bi ${i <= product.averageRating ? 'bi-star-fill' : (i - 0.5 <= product.averageRating ? 'bi-star-half' : 'bi-star')}"></i>
                    </c:forEach>
                </span>
                <small class="text-secondary ms-1">(${product.reviewCount})</small>
            </div>

            <!-- Price & Availability -->
            <div class="d-flex align-items-baseline mb-3">
                <span class="ss-price-tag">₹<fmt:formatNumber value="${product.effectivePrice}" minFractionDigits="2"/></span>
                <c:if test="${product.discount > 0}">
                    <span class="ss-original-price">₹<fmt:formatNumber value="${product.price}" minFractionDigits="2"/></span>
                </c:if>
            </div>

            <!-- Add to Cart Action -->
            <div class="mt-auto">
                <c:choose>
                    <c:when test="${product.inStock}">
                        <form action="${pageContext.request.contextPath}/cart/add" method="POST">
                            <input type="hidden" name="productId" value="${product.productId}">
                            <input type="hidden" name="quantity" value="1">
                            <button type="submit" class="btn btn-ss-primary w-100 btn-sm">
                                <i class="bi bi-cart-plus me-1"></i> Add to Cart
                            </button>
                        </form>
                    </c:when>
                    <c:otherwise>
                        <button class="btn btn-secondary w-100 btn-sm" disabled>
                            <i class="bi bi-x-circle me-1"></i> Out of Stock
                        </button>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</div>
