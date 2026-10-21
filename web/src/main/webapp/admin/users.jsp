<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="pageTitle" value="Customer Management | Admin" scope="request"/>
<jsp:include page="/fragments/header.jsp"/>
<jsp:include page="/fragments/navbar.jsp"/>

<div class="container-fluid p-0">
    <div class="row g-0">
        <jsp:include page="/fragments/sidebar.jsp">
            <jsp:param name="activeNav" value="users"/>
        </jsp:include>

        <div class="col-lg-9 col-md-8 p-4">
            <div class="d-flex justify-content-between align-items-center mb-4">
                <div>
                    <h3 class="fw-bold mb-1">Customer Accounts</h3>
                    <p class="text-secondary small mb-0">View registered users, inspect roles, and toggle active/inactive account status</p>
                </div>
            </div>

            <!-- Customer Search -->
            <div class="card border-0 shadow-sm rounded-4 p-3 mb-4 bg-white">
                <form action="${pageContext.request.contextPath}/admin/users" method="GET" class="row g-2">
                    <div class="col-md-10">
                        <input type="text" name="q" class="form-control form-control-sm" placeholder="Search customer by name, email, or mobile..." value="${searchKeyword}">
                    </div>
                    <div class="col-md-2">
                        <button type="submit" class="btn btn-primary btn-sm w-100">Search</button>
                    </div>
                </form>
            </div>

            <!-- Users Table -->
            <div class="card border-0 shadow-sm rounded-4 p-4 bg-white">
                <div class="table-responsive">
                    <table class="table align-middle table-hover small">
                        <thead class="text-secondary text-uppercase">
                            <tr>
                                <th>ID</th>
                                <th>Name</th>
                                <th>Email</th>
                                <th>Mobile</th>
                                <th>Role</th>
                                <th>Status</th>
                                <th>Registered</th>
                                <th class="text-end">Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${users}" var="u">
                                <tr>
                                    <td class="fw-bold">${u.userId}</td>
                                    <td class="fw-semibold text-dark">${u.name}</td>
                                    <td>${u.email}</td>
                                    <td>${not empty u.mobile ? u.mobile : '—'}</td>
                                    <td>
                                        <span class="badge ${u.role == 'ADMIN' ? 'bg-primary' : 'bg-secondary'} rounded-pill">
                                            ${u.role}
                                        </span>
                                    </td>
                                    <td>
                                        <span class="badge ${u.status == 'ACTIVE' ? 'bg-success-subtle text-success' : 'bg-danger-subtle text-danger'} rounded-pill">
                                            ${u.status}
                                        </span>
                                    </td>
                                    <td class="text-muted"><fmt:formatDate value="${u.createdAt}" pattern="dd MMM yyyy"/></td>
                                    <td class="text-end">
                                        <form action="${pageContext.request.contextPath}/admin/users/toggle-status" method="POST" class="d-inline">
                                            <input type="hidden" name="userId" value="${u.userId}">
                                            <input type="hidden" name="status" value="${u.status == 'ACTIVE' ? 'INACTIVE' : 'ACTIVE'}">
                                            <button type="submit" class="btn btn-sm ${u.status == 'ACTIVE' ? 'btn-outline-danger' : 'btn-outline-success'} py-0 px-2">
                                                ${u.status == 'ACTIVE' ? 'Deactivate' : 'Activate'}
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

<jsp:include page="/fragments/footer.jsp"/>
