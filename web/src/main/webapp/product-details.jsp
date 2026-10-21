<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<c:set var="pageTitle" value="${product.name} | ShopSphere" scope="request"/>
<jsp:include page="/fragments/header.jsp"/>
<jsp:include page="/fragments/navbar.jsp"/>

<main class="py-4">
    <div class="container">
        <!-- Breadcrumb -->
        <nav aria-label="breadcrumb" class="mb-4">
            <ol class="breadcrumb">
                <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/home">Home</a></li>
                <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/products">Products</a></li>
                <li class="breadcrumb-item active" aria-current="page">${product.name}</li>
            </ol>
        </nav>

        <div class="card border-0 shadow-sm rounded-4 p-4 p-md-5 mb-5">
            <div class="row g-5">
                <!-- Product Image Gallery / Display -->
                <div class="col-lg-5 text-center">
                    <div class="bg-white p-4 rounded-4 border border-light-subtle d-flex align-items-center justify-content-center" style="min-height: 380px;">
                        <img src="${not empty product.imageUrl ? product.imageUrl : 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=600'}" 
                             class="img-fluid rounded-3" alt="${product.name}" style="max-height: 350px; object-fit: contain;">
                    </div>
                </div>

                <!-- Product Purchasing Info -->
                <div class="col-lg-7">
                    <span class="badge bg-primary-subtle text-primary px-3 py-1 rounded-pill mb-2 fw-bold text-uppercase">
                        ${product.categoryName} &bull; ${product.brand}
                    </span>
                    <h2 class="fw-bold mb-2">${product.name}</h2>

                    <!-- Star Ratings -->
                    <div class="d-flex align-items-center gap-2 mb-3">
                        <tags:rating rating="${product.averageRating}" showNumber="true"/>
                        <span class="text-secondary">&bull;</span>
                        <span class="text-secondary small">${reviews.size()} Customer Reviews</span>
                    </div>

                    <!-- Pricing Block -->
                    <div class="p-3 rounded-3 bg-light d-flex align-items-baseline gap-3 mb-4">
                        <span class="fs-2 fw-bolder text-primary">₹<fmt:formatNumber value="${product.effectivePrice}" minFractionDigits="2"/></span>
                        <c:if test="${product.discount > 0}">
                            <span class="text-decoration-line-through text-muted fs-5">₹<fmt:formatNumber value="${product.price}" minFractionDigits="2"/></span>
                            <span class="badge bg-danger">SAVE ₹<fmt:formatNumber value="${product.discount}" minFractionDigits="0"/></span>
                        </c:if>
                    </div>

                    <!-- Product Description -->
                    <h6 class="fw-bold text-uppercase small text-secondary">Product Overview</h6>
                    <p class="text-secondary leading-relaxed mb-4">${product.description}</p>

                    <!-- Stock Status Indicator -->
                    <div class="mb-4">
                        <c:choose>
                            <c:when test="${product.inStock}">
                                <span class="badge bg-success-subtle text-success fs-6 px-3 py-2 rounded-pill">
                                    <i class="bi bi-check-circle-fill me-1"></i> In Stock (${product.stock} units available)
                                </span>
                            </c:when>
                            <c:otherwise>
                                <span class="badge bg-danger-subtle text-danger fs-6 px-3 py-2 rounded-pill">
                                    <i class="bi bi-x-circle-fill me-1"></i> Currently Out of Stock
                                </span>
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <!-- Add to Cart Form -->
                    <c:if test="${product.inStock}">
                        <form action="${pageContext.request.contextPath}/cart/add" method="POST" class="d-flex align-items-center gap-3 mb-4 flex-wrap">
                            <input type="hidden" name="productId" value="${product.productId}">
                            <div style="width: 110px;">
                                <label class="form-label small fw-semibold text-secondary">Quantity</label>
                                <input type="number" name="quantity" class="form-control" value="1" min="1" max="${product.stock}" required>
                            </div>
                            <div class="align-self-end flex-grow-1">
                                <button type="submit" class="btn btn-ss-primary btn-lg w-100">
                                    <i class="bi bi-cart-plus me-2"></i> Add to Shopping Cart
                                </button>
                            </div>
                        </form>
                    </c:if>

                    <!-- Action buttons -->
                    <div class="d-flex gap-3">
                        <form action="${pageContext.request.contextPath}/wishlist/add" method="POST">
                            <input type="hidden" name="productId" value="${product.productId}">
                            <button type="submit" class="btn btn-outline-secondary">
                                <i class="bi bi-heart me-1"></i> Save to Wishlist
                            </button>
                        </form>
                    </div>
                </div>
            </div>
        </div>

        <!-- Reviews & Ratings Section -->
        <div class="row g-4 mb-5">
            <div class="col-lg-8">
                <div class="card border-0 shadow-sm rounded-4 p-4">
                    <h4 class="fw-bold mb-4">Customer Reviews (${reviews.size()})</h4>

                    <c:choose>
                        <c:when test="${not empty reviews}">
                            <div class="d-flex flex-column gap-3">
                                <c:forEach items="${reviews}" var="rev">
                                    <div class="p-3 border rounded-3 bg-light-subtle">
                                        <div class="d-flex justify-content-between align-items-center mb-2">
                                            <div class="d-flex align-items-center gap-2">
                                                <i class="bi bi-person-circle fs-4 text-primary"></i>
                                                <span class="fw-bold">${rev.userName}</span>
                                            </div>
                                            <tags:rating rating="${rev.rating * 1.0}"/>
                                        </div>
                                        <p class="mb-1 text-secondary">${rev.reviewText}</p>
                                        <small class="text-muted"><fmt:formatDate value="${rev.createdAt}" pattern="dd MMM yyyy, hh:mm a"/></small>
                                    </div>
                                </c:forEach>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <p class="text-muted">No reviews yet for this product. Be the first to share your thoughts!</p>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>

            <!-- Submit Review Form Card -->
            <div class="col-lg-4">
                <div class="card border-0 shadow-sm rounded-4 p-4">
                    <h5 class="fw-bold mb-3">Write a Review</h5>
                    <c:choose>
                        <c:when test="${not empty sessionScope.currentUser}">
                            <c:choose>
                                <c:when test="${canReview}">
                                    <form action="${pageContext.request.contextPath}/review/submit" method="POST">
                                        <input type="hidden" name="productId" value="${product.productId}">

                                        <div class="mb-3">
                                            <label class="form-label fw-semibold small">Rating</label>
                                            <select name="rating" class="form-select" required>
                                                <option value="5">⭐⭐⭐⭐⭐ (5 - Outstanding)</option>
                                                <option value="4">⭐⭐⭐⭐ (4 - Very Good)</option>
                                                <option value="3">⭐⭐⭐ (3 - Average)</option>
                                                <option value="2">⭐⭐ (2 - Below Expectations)</option>
                                                <option value="1">⭐ (1 - Poor)</option>
                                            </select>
                                        </div>

                                        <div class="mb-3">
                                            <label class="form-label fw-semibold small">Review Feedback</label>
                                            <textarea name="reviewText" rows="4" class="form-control" 
                                                      placeholder="How was the build quality, performance, or packaging?" required></textarea>
                                        </div>

                                        <button type="submit" class="btn btn-ss-primary w-100">Post Review</button>
                                    </form>
                                </c:when>
                                <c:otherwise>
                                    <div class="alert alert-info small mb-0">
                                        <i class="bi bi-check2-circle me-1"></i> You have already submitted a review for this product.
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </c:when>
                        <c:otherwise>
                            <div class="alert alert-secondary small mb-0">
                                Please <a href="${pageContext.request.contextPath}/login" class="fw-bold">Sign In</a> to write a product review.
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>

        <!-- Recently Viewed Products (Cookie demonstration) -->
        <c:if test="${not empty recentlyViewed}">
            <div class="mt-5">
                <h4 class="fw-bold mb-3"><i class="bi bi-clock-history text-primary me-2"></i>Recently Viewed (Saved in Cookie)</h4>
                <div class="row">
                    <c:forEach items="${recentlyViewed}" var="rec">
                        <tags:productCard product="${rec}"/>
                    </c:forEach>
                </div>
            </div>
        </c:if>
    </div>
</main>

<jsp:include page="/fragments/footer.jsp"/>
