<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="pageTitle" value="Coupon Management | Admin" scope="request"/>
<jsp:include page="/fragments/header.jsp"/>
<jsp:include page="/fragments/navbar.jsp"/>

<div class="container-fluid p-0">
    <div class="row g-0">
        <jsp:include page="/fragments/sidebar.jsp">
            <jsp:param name="activeNav" value="coupons"/>
        </jsp:include>

        <div class="col-lg-9 col-md-8 p-4">
            <div class="d-flex justify-content-between align-items-center mb-4">
                <div>
                    <h3 class="fw-bold mb-1">Discount Coupons</h3>
                    <p class="text-secondary small mb-0">Configure promotional vouchers, maximum caps, expiry dates, and minimum cart thresholds</p>
                </div>
                <button type="button" class="btn btn-ss-primary btn-sm rounded-pill px-3" data-bs-toggle="modal" data-bs-target="#couponModal" onclick="clearCouponModal()">
                    <i class="bi bi-plus-lg me-1"></i> Create Coupon
                </button>
            </div>

            <div class="card border-0 shadow-sm rounded-4 p-4 bg-white">
                <div class="table-responsive">
                    <table class="table align-middle table-hover small">
                        <thead class="text-secondary text-uppercase">
                            <tr>
                                <th>Code</th>
                                <th>Type</th>
                                <th>Value</th>
                                <th>Min Order</th>
                                <th>Max Cap</th>
                                <th>Expiry Date</th>
                                <th>Status</th>
                                <th class="text-end">Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${coupons}" var="cp">
                                <tr>
                                    <td class="fw-bold font-monospace text-primary">${cp.code}</td>
                                    <td><span class="badge bg-light text-dark border">${cp.discountType}</span></td>
                                    <td class="fw-bold">
                                        <c:choose>
                                            <c:when test="${cp.discountType == 'PERCENTAGE'}">${cp.discountValue}%</c:when>
                                            <c:otherwise>₹<fmt:formatNumber value="${cp.discountValue}" minFractionDigits="2"/></c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>₹<fmt:formatNumber value="${cp.minimumOrder}" minFractionDigits="2"/></td>
                                    <td>₹<fmt:formatNumber value="${cp.maximumDiscount}" minFractionDigits="2"/></td>
                                    <td>
                                        <span class="${cp.expired ? 'text-danger fw-bold' : ''}">
                                            ${cp.expiryDate} ${cp.expired ? '(Expired)' : ''}
                                        </span>
                                    </td>
                                    <td>
                                        <span class="badge ${cp.active ? 'bg-success' : 'bg-secondary'} rounded-pill">
                                            ${cp.status}
                                        </span>
                                    </td>
                                    <td class="text-end">
                                        <form action="${pageContext.request.contextPath}/admin/coupons/delete" method="POST" class="d-inline"
                                              onsubmit="return confirm('Delete this coupon?');">
                                            <input type="hidden" name="couponId" value="${cp.couponId}">
                                            <button type="submit" class="btn btn-sm btn-outline-danger py-0 px-2">
                                                <i class="bi bi-trash"></i>
                                            </button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Coupon Modal -->
<div class="modal fade" id="couponModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content border-0 rounded-4 shadow">
            <div class="modal-header border-0 pb-0">
                <h5 class="modal-title fw-bold" id="couponModalTitle">Create Promo Coupon</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <form action="${pageContext.request.contextPath}/admin/coupons/save" method="POST">
                <div class="modal-body">
                    <input type="hidden" name="couponId" id="modalCouponId" value="">

                    <div class="row g-2 mb-3">
                        <div class="col-6">
                            <label class="form-label small fw-semibold">Coupon Code</label>
                            <input type="text" name="code" id="modalCode" class="form-control text-uppercase" placeholder="SUMMER50" required>
                        </div>
                        <div class="col-6">
                            <label class="form-label small fw-semibold">Discount Type</label>
                            <select name="discountType" id="modalDiscountType" class="form-select">
                                <option value="PERCENTAGE">PERCENTAGE (%)</option>
                                <option value="FIXED">FIXED (₹)</option>
                            </select>
                        </div>
                    </div>

                    <div class="row g-2 mb-3">
                        <div class="col-4">
                            <label class="form-label small fw-semibold">Discount Value</label>
                            <input type="number" step="0.01" name="discountValue" id="modalDiscountVal" class="form-control" required>
                        </div>
                        <div class="col-4">
                            <label class="form-label small fw-semibold">Min Order (₹)</label>
                            <input type="number" step="0.01" name="minimumOrder" id="modalMinOrder" class="form-control" value="0.00" required>
                        </div>
                        <div class="col-4">
                            <label class="form-label small fw-semibold">Max Cap (₹)</label>
                            <input type="number" step="0.01" name="maximumDiscount" id="modalMaxCap" class="form-control" value="0.00" required>
                        </div>
                    </div>

                    <div class="row g-2 mb-3">
                        <div class="col-6">
                            <label class="form-label small fw-semibold">Expiry Date</label>
                            <input type="date" name="expiryDate" id="modalExpiry" class="form-control" required>
                        </div>
                        <div class="col-6">
                            <label class="form-label small fw-semibold">Status</label>
                            <select name="status" id="modalCouponStatus" class="form-select">
                                <option value="ACTIVE">ACTIVE</option>
                                <option value="INACTIVE">INACTIVE</option>
                            </select>
                        </div>
                    </div>
                </div>
                <div class="modal-footer border-0 pt-0">
                    <button type="button" class="btn btn-secondary btn-sm" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-ss-primary btn-sm px-4">Save Coupon</button>
                </div>
            </form>
        </div>
    </div>
</div>

<script>
function clearCouponModal() {
    document.getElementById('modalCouponId').value = '';
    document.getElementById('modalCode').value = '';
    document.getElementById('modalDiscountVal').value = '';
    document.getElementById('modalMinOrder').value = '0.00';
    document.getElementById('modalMaxCap').value = '0.00';
    document.getElementById('modalExpiry').value = new Date(Date.now() + 30*24*60*60*1000).toISOString().split('T')[0];
    document.getElementById('modalCouponStatus').value = 'ACTIVE';
}
</script>

<jsp:include page="/fragments/footer.jsp"/>
