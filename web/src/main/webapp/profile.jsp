<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="pageTitle" value="My Account | ShopSphere" scope="request"/>
<jsp:include page="/fragments/header.jsp"/>
<jsp:include page="/fragments/navbar.jsp"/>

<main class="py-4">
    <div class="container">
        <h2 class="fw-bold mb-4"><i class="bi bi-person-circle text-primary me-2"></i>Account &amp; Settings</h2>

        <div class="row g-4">
            <!-- Left Column: Personal Info & Password -->
            <div class="col-lg-5">
                <!-- Personal Info Card -->
                <div class="card border-0 shadow-sm rounded-4 p-4 mb-4">
                    <h5 class="fw-bold mb-3">Profile Information</h5>
                    <form action="${pageContext.request.contextPath}/profile/update" method="POST">
                        <div class="mb-3">
                            <label class="form-label small fw-semibold">Email Address</label>
                            <input type="email" class="form-control" value="${user.email}" disabled>
                            <small class="text-muted">Email cannot be modified.</small>
                        </div>
                        <div class="mb-3">
                            <label class="form-label small fw-semibold">Full Name</label>
                            <input type="text" name="name" class="form-control" value="${user.name}" required>
                        </div>
                        <div class="mb-4">
                            <label class="form-label small fw-semibold">Mobile Number</label>
                            <input type="tel" name="mobile" class="form-control" value="${user.mobile}" placeholder="10-digit mobile">
                        </div>
                        <button type="submit" class="btn btn-ss-primary btn-sm px-3">Update Details</button>
                    </form>
                </div>

                <!-- Change Password Card -->
                <div class="card border-0 shadow-sm rounded-4 p-4">
                    <h5 class="fw-bold mb-3">Change Password</h5>
                    <form action="${pageContext.request.contextPath}/profile/change-password" method="POST">
                        <div class="mb-3">
                            <label class="form-label small fw-semibold">Current Password</label>
                            <input type="password" name="oldPassword" class="form-control form-control-sm" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label small fw-semibold">New Password</label>
                            <input type="password" name="newPassword" class="form-control form-control-sm" placeholder="Min 6 characters" required>
                        </div>
                        <button type="submit" class="btn btn-outline-primary btn-sm px-3">Update Password</button>
                    </form>
                </div>
            </div>

            <!-- Right Column: Address Book -->
            <div class="col-lg-7">
                <div class="card border-0 shadow-sm rounded-4 p-4 mb-4">
                    <div class="d-flex justify-content-between align-items-center mb-3">
                        <h5 class="fw-bold mb-0">Delivery Address Book</h5>
                        <button type="button" class="btn btn-ss-primary btn-sm rounded-pill" data-bs-toggle="modal" data-bs-target="#addressModal" onclick="clearAddressForm()">
                            <i class="bi bi-plus-lg me-1"></i> Add New Address
                        </button>
                    </div>

                    <c:choose>
                        <c:when test="${not empty addresses}">
                            <div class="row g-3">
                                <c:forEach items="${addresses}" var="a">
                                    <div class="col-md-6">
                                        <div class="card p-3 border rounded-3 h-100 ${a.default ? 'border-primary bg-primary-subtle' : ''}">
                                            <div class="d-flex justify-content-between align-items-start mb-2">
                                                <span class="badge bg-secondary">${a.addressType}</span>
                                                <c:if test="${a.default}">
                                                    <span class="badge bg-primary">DEFAULT</span>
                                                </c:if>
                                            </div>
                                            <div class="small text-dark fw-semibold mb-1">${a.addressLine}</div>
                                            <div class="small text-secondary mb-3">${a.city}, ${a.state} - ${a.pincode}</div>
                                            <div class="mt-auto d-flex gap-2">
                                                <form action="${pageContext.request.contextPath}/address/delete" method="POST" onsubmit="return confirm('Delete this address?');">
                                                    <input type="hidden" name="addressId" value="${a.addressId}">
                                                    <button type="submit" class="btn btn-link text-danger p-0 small text-decoration-none">Delete</button>
                                                </form>
                                            </div>
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <p class="text-secondary small mb-0">No saved addresses. Click above to add your home or work delivery location.</p>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
    </div>
</main>

<!-- Add / Edit Address Modal -->
<div class="modal fade" id="addressModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content border-0 rounded-4 shadow">
            <div class="modal-header border-0 pb-0">
                <h5 class="modal-title fw-bold" id="addressModalTitle">Add Delivery Address</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <form action="${pageContext.request.contextPath}/address/save" method="POST">
                <div class="modal-body">
                    <input type="hidden" name="addressId" id="modalAddressId" value="">

                    <div class="mb-3">
                        <label class="form-label small fw-semibold">Address Line / Street</label>
                        <input type="text" name="addressLine" id="modalAddressLine" class="form-control" placeholder="House no, Street, Landmark" required>
                    </div>
                    <div class="row g-2 mb-3">
                        <div class="col-6">
                            <label class="form-label small fw-semibold">City</label>
                            <input type="text" name="city" id="modalCity" class="form-control" placeholder="City" required>
                        </div>
                        <div class="col-6">
                            <label class="form-label small fw-semibold">State</label>
                            <input type="text" name="state" id="modalState" class="form-control" placeholder="State" required>
                        </div>
                    </div>
                    <div class="row g-2 mb-3">
                        <div class="col-6">
                            <label class="form-label small fw-semibold">Pincode</label>
                            <input type="text" name="pincode" id="modalPincode" class="form-control" placeholder="6-digit PIN" required>
                        </div>
                        <div class="col-6">
                            <label class="form-label small fw-semibold">Address Type</label>
                            <select name="addressType" id="modalAddressType" class="form-select">
                                <option value="HOME">HOME</option>
                                <option value="WORK">WORK</option>
                                <option value="OTHER">OTHER</option>
                            </select>
                        </div>
                    </div>
                    <div class="form-check">
                        <input class="form-check-input" type="checkbox" name="isDefault" id="modalIsDefault">
                        <label class="form-check-label small" for="modalIsDefault">Set as default shipping address</label>
                    </div>
                </div>
                <div class="modal-footer border-0 pt-0">
                    <button type="button" class="btn btn-secondary btn-sm" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-ss-primary btn-sm px-3">Save Address</button>
                </div>
            </form>
        </div>
    </div>
</div>

<script>
function clearAddressForm() {
    document.getElementById('modalAddressId').value = '';
    document.getElementById('modalAddressLine').value = '';
    document.getElementById('modalCity').value = '';
    document.getElementById('modalState').value = '';
    document.getElementById('modalPincode').value = '';
    document.getElementById('modalIsDefault').checked = false;
}
</script>

<jsp:include page="/fragments/footer.jsp"/>
