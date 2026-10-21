<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:set var="pageTitle" value="Sign In | ShopSphere" scope="request"/>
<jsp:include page="/fragments/header.jsp"/>
<jsp:include page="/fragments/navbar.jsp"/>

<main class="py-5">
    <div class="container">
        <div class="row justify-content-center">
            <div class="col-md-6 col-lg-5">
                <div class="card border-0 shadow-lg rounded-4 p-4 p-md-5">
                    <div class="text-center mb-4">
                        <div class="bg-primary-subtle text-primary p-3 rounded-circle d-inline-block mb-3 fs-3">
                            <i class="bi bi-shield-lock-fill"></i>
                        </div>
                        <h3 class="fw-bold">Sign In to ShopSphere</h3>
                        <p class="text-muted small">Enter your verified credentials to access your persistent cart and orders</p>
                    </div>

                    <form action="${pageContext.request.contextPath}/login" method="POST">
                        <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">

                        <div class="mb-3">
                            <label class="form-label fw-semibold">Email Address</label>
                            <div class="input-group">
                                <span class="input-group-text bg-light border-end-0"><i class="bi bi-envelope"></i></span>
                                <input type="email" name="email" class="form-control border-start-0" 
                                       placeholder="you@domain.com" required autofocus>
                            </div>
                        </div>

                        <div class="mb-4">
                            <label class="form-label fw-semibold">Password</label>
                            <div class="input-group">
                                <span class="input-group-text bg-light border-end-0"><i class="bi bi-key"></i></span>
                                <input type="password" name="password" class="form-control border-start-0" 
                                       placeholder="••••••••" required>
                            </div>
                        </div>

                        <button type="submit" class="btn btn-ss-primary w-100 py-2 fw-bold">
                            <i class="bi bi-box-arrow-in-right me-2"></i> Continue
                        </button>
                    </form>

                    <!-- Capstone Test Credentials Box -->
                    <div class="mt-4 p-3 rounded-3 bg-light border text-start">
                        <h6 class="fw-bold mb-2 small text-uppercase text-secondary">
                            <i class="bi bi-info-circle-fill text-primary me-1"></i> Demo Evaluation Logins:
                        </h6>
                        <div class="small">
                            <strong>Administrator:</strong> <code>admin@shopsphere.com</code> / <code>Admin@123</code><br>
                            <strong>Customer:</strong> <code>customer@shopsphere.com</code> / <code>Customer@123</code>
                        </div>
                    </div>

                    <div class="text-center mt-4">
                        <span class="text-muted small">Don't have an account? </span>
                        <a href="${pageContext.request.contextPath}/register" class="fw-bold text-decoration-none">Register here</a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</main>

<jsp:include page="/fragments/footer.jsp"/>
