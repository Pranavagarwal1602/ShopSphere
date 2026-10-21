<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:set var="pageTitle" value="Create Account | ShopSphere" scope="request"/>
<jsp:include page="/fragments/header.jsp"/>
<jsp:include page="/fragments/navbar.jsp"/>

<main class="py-5">
    <div class="container">
        <div class="row justify-content-center">
            <div class="col-md-7 col-lg-6">
                <div class="card border-0 shadow-lg rounded-4 p-4 p-md-5">
                    <div class="text-center mb-4">
                        <div class="bg-primary-subtle text-primary p-3 rounded-circle d-inline-block mb-3 fs-3">
                            <i class="bi bi-person-plus-fill"></i>
                        </div>
                        <h3 class="fw-bold">Create Customer Account</h3>
                        <p class="text-muted small">Register to save multiple shipping addresses, manage orders, and write reviews</p>
                    </div>

                    <form action="${pageContext.request.contextPath}/register" method="POST">
                        <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">

                        <div class="mb-3">
                            <label class="form-label fw-semibold">Full Name</label>
                            <input type="text" name="name" class="form-control" value="${name}" placeholder="John Doe" required>
                        </div>

                        <div class="mb-3">
                            <label class="form-label fw-semibold">Email Address</label>
                            <input type="email" name="email" class="form-control" value="${email}" placeholder="john@example.com" required>
                        </div>

                        <div class="row g-3 mb-3">
                            <div class="col-md-6">
                                <label class="form-label fw-semibold">Password</label>
                                <input type="password" name="password" class="form-control" placeholder="Min 6 characters" required>
                            </div>
                            <div class="col-md-6">
                                <label class="form-label fw-semibold">Confirm Password</label>
                                <input type="password" name="confirmPassword" class="form-control" placeholder="Repeat password" required>
                            </div>
                        </div>

                        <div class="mb-4">
                            <label class="form-label fw-semibold">Mobile Number</label>
                            <input type="tel" name="mobile" class="form-control" value="${mobile}" placeholder="10-digit mobile number">
                        </div>

                        <button type="submit" class="btn btn-ss-primary w-100 py-2 fw-bold">
                            <i class="bi bi-check2-circle me-1"></i> Register Account
                        </button>
                    </form>

                    <div class="text-center mt-4">
                        <span class="text-muted small">Already registered? </span>
                        <a href="${pageContext.request.contextPath}/login" class="fw-bold text-decoration-none">Sign In</a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</main>

<jsp:include page="/fragments/footer.jsp"/>
