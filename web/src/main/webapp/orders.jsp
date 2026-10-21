<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="pageTitle" value="My Orders | ShopSphere" scope="request"/>
<jsp:include page="/fragments/header.jsp"/>
<jsp:include page="/fragments/navbar.jsp"/>

<main class="py-4">
    <div class="container">
        <h2 class="fw-bold mb-4"><i class="bi bi-bag-check text-primary me-2"></i>My Orders &amp; Tracking</h2>

        <c:choose>
            <c:when test="${not empty orders}">
                <div class="d-flex flex-column gap-3">
                    <c:forEach items="${orders}" var="ord">
                        <div class="card border-0 shadow-sm rounded-4 p-4">
                            <div class="d-flex justify-content-between align-items-center flex-wrap gap-2 pb-3 mb-3 border-bottom">
                                <div>
                                    <span class="fw-bold fs-5 text-dark">Order #${ord.orderId}</span>
                                    <div class="small text-muted"><fmt:formatDate value="${ord.createdAt}" pattern="dd MMM yyyy, hh:mm a"/></div>
                                </div>
                                <div class="text-end">
                                    <span class="badge ${ord.orderStatus == 'DELIVERED' ? 'bg-success' : (ord.orderStatus == 'CANCELLED' ? 'bg-danger' : 'bg-primary')} px-3 py-2 rounded-pill fs-6">
                                        ${ord.orderStatus}
                                    </span>
                                </div>
                            </div>

                            <div class="row align-items-center">
                                <div class="col-md-8">
                                    <div class="small text-secondary mb-2">
                                        <strong>Items (${ord.items.size()}):</strong>
                                        <c:forEach items="${ord.items}" var="it" varStatus="s">
                                            ${it.product.name} (x${it.quantity})${!s.last ? ', ' : ''}
                                        </c:forEach>
                                    </div>
                                    <div class="small text-secondary">
                                        <strong>Payment:</strong> ${ord.paymentMethod} &bull; <span class="badge bg-secondary">${ord.paymentStatus}</span>
                                    </div>
                                </div>
                                <div class="col-md-4 text-md-end mt-3 mt-md-0">
                                    <div class="fs-5 fw-bolder text-primary mb-2">
                                        ₹<fmt:formatNumber value="${ord.totalAmount}" minFractionDigits="2"/>
                                    </div>
                                    <a href="${pageContext.request.contextPath}/order-details?id=${ord.orderId}" class="btn btn-outline-primary btn-sm rounded-pill px-3">
                                        View Timeline &amp; Receipt <i class="bi bi-chevron-right ms-1"></i>
                                    </a>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </c:when>
            <c:otherwise>
                <div class="card border-0 shadow-sm rounded-4 p-5 text-center my-4">
                    <i class="bi bi-receipt fs-1 text-secondary mb-3"></i>
                    <h4 class="fw-bold">You Haven't Placed Any Orders Yet</h4>
                    <p class="text-secondary mb-4">Start browsing the store and place your first order.</p>
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
