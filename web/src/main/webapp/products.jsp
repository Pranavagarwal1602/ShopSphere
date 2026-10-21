<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<c:set var="pageTitle" value="Product Catalog | ShopSphere" scope="request"/>
<jsp:include page="/fragments/header.jsp"/>
<jsp:include page="/fragments/navbar.jsp"/>

<main class="py-4">
    <div class="container">
        <!-- Breadcrumb -->
        <nav aria-label="breadcrumb" class="mb-4">
            <ol class="breadcrumb">
                <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/home">Home</a></li>
                <li class="breadcrumb-item active" aria-current="page">Catalog</li>
            </ol>
        </nav>

        <div class="row g-4">
            <!-- Sidebar Filters -->
            <div class="col-lg-3">
                <div class="card border-0 shadow-sm rounded-4 p-4 sticky-top" style="top: 85px;">
                    <h5 class="fw-bold mb-3"><i class="bi bi-funnel-fill text-primary me-2"></i>Filter Products</h5>
                    <form action="${pageContext.request.contextPath}/products" method="GET">
                        <c:if test="${not empty query}">
                            <input type="hidden" name="q" value="<c:out value='${query}'/>">
                        </c:if>

                        <!-- Category Filter -->
                        <div class="mb-4">
                            <label class="form-label fw-semibold small text-uppercase text-secondary">Department</label>
                            <select name="category" class="form-select">
                                <option value="">All Categories</option>
                                <c:forEach items="${categories}" var="cat">
                                    <option value="${cat.categoryId}" ${selectedCategory == cat.categoryId ? 'selected' : ''}>
                                        ${cat.name}
                                    </option>
                                </c:forEach>
                            </select>
                        </div>

                        <!-- Price Range Filter -->
                        <div class="mb-4">
                            <label class="form-label fw-semibold small text-uppercase text-secondary">Price Range (₹)</label>
                            <div class="row g-2">
                                <div class="col-6">
                                    <input type="number" name="minPrice" class="form-control form-control-sm" 
                                           placeholder="Min" value="${minPrice}">
                                </div>
                                <div class="col-6">
                                    <input type="number" name="maxPrice" class="form-control form-control-sm" 
                                           placeholder="Max" value="${maxPrice}">
                                </div>
                            </div>
                        </div>

                        <!-- Sort Order -->
                        <div class="mb-4">
                            <label class="form-label fw-semibold small text-uppercase text-secondary">Sort By</label>
                            <select name="sort" class="form-select form-select-sm">
                                <option value="" ${empty selectedSort ? 'selected' : ''}>Newest Arrivals</option>
                                <option value="price_asc" ${selectedSort == 'price_asc' ? 'selected' : ''}>Price: Low to High</option>
                                <option value="price_desc" ${selectedSort == 'price_desc' ? 'selected' : ''}>Price: High to Low</option>
                                <option value="rating" ${selectedSort == 'rating' ? 'selected' : ''}>Highest Customer Rating</option>
                                <option value="name_asc" ${selectedSort == 'name_asc' ? 'selected' : ''}>Alphabetical (A - Z)</option>
                            </select>
                        </div>

                        <button type="submit" class="btn btn-ss-primary w-100 btn-sm mb-2">Apply Filters</button>
                        <a href="${pageContext.request.contextPath}/products" class="btn btn-outline-secondary w-100 btn-sm">Reset</a>
                    </form>
                </div>
            </div>

            <!-- Product Results Grid -->
            <div class="col-lg-9">
                <div class="d-flex justify-content-between align-items-center mb-3">
                    <span class="text-secondary small">
                        Showing <strong>${products.size()}</strong> of <strong>${totalProducts}</strong> products found
                    </span>
                </div>

                <c:choose>
                    <c:when test="${not empty products}">
                        <div class="row">
                            <c:forEach items="${products}" var="prod">
                                <tags:productCard product="${prod}"/>
                            </c:forEach>
                        </div>

                        <!-- Pagination Controls -->
                        <c:if test="${totalPages > 1}">
                            <nav class="mt-4" aria-label="Catalog pagination">
                                <ul class="pagination justify-content-center">
                                    <li class="page-item ${currentPage <= 1 ? 'disabled' : ''}">
                                        <a class="page-link" href="${pageContext.request.contextPath}/products?page=${currentPage - 1}&category=${selectedCategory}&minPrice=${minPrice}&maxPrice=${maxPrice}&sort=${selectedSort}&q=${query}">Previous</a>
                                    </li>
                                    <c:forEach begin="1" end="${totalPages}" var="p">
                                        <li class="page-item ${currentPage == p ? 'active' : ''}">
                                            <a class="page-link" href="${pageContext.request.contextPath}/products?page=${p}&category=${selectedCategory}&minPrice=${minPrice}&maxPrice=${maxPrice}&sort=${selectedSort}&q=${query}">${p}</a>
                                        </li>
                                    </c:forEach>
                                    <li class="page-item ${currentPage >= totalPages ? 'disabled' : ''}">
                                        <a class="page-link" href="${pageContext.request.contextPath}/products?page=${currentPage + 1}&category=${selectedCategory}&minPrice=${minPrice}&maxPrice=${maxPrice}&sort=${selectedSort}&q=${query}">Next</a>
                                    </li>
                                </ul>
                            </nav>
                        </c:if>
                    </c:when>
                    <c:otherwise>
                        <!-- Empty Search State -->
                        <div class="card border-0 shadow-sm rounded-4 p-5 text-center my-4">
                            <i class="bi bi-search fs-1 text-secondary mb-3"></i>
                            <h4 class="fw-bold">No Products Matched Your Criteria</h4>
                            <p class="text-secondary mb-4">Try clearing some filters or searching for generic keywords like "phone", "audio", or "shoes".</p>
                            <div>
                                <a href="${pageContext.request.contextPath}/products" class="btn btn-ss-primary">
                                    View Full Catalog
                                </a>
                            </div>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</main>

<jsp:include page="/fragments/footer.jsp"/>
