<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:set var="pageTitle" value="Category Management | Admin" scope="request"/>
<jsp:include page="/fragments/header.jsp"/>
<jsp:include page="/fragments/navbar.jsp"/>

<div class="container-fluid p-0">
    <div class="row g-0">
        <jsp:include page="/fragments/sidebar.jsp">
            <jsp:param name="activeNav" value="categories"/>
        </jsp:include>

        <div class="col-lg-9 col-md-8 p-4">
            <div class="d-flex justify-content-between align-items-center mb-4">
                <div>
                    <h3 class="fw-bold mb-1">Categories</h3>
                    <p class="text-secondary small mb-0">Manage e-commerce store catalog hierarchies and departments</p>
                </div>
                <button type="button" class="btn btn-ss-primary btn-sm rounded-pill px-3" data-bs-toggle="modal" data-bs-target="#categoryModal" onclick="clearCatModal()">
                    <i class="bi bi-plus-lg me-1"></i> Add Category
                </button>
            </div>

            <div class="card border-0 shadow-sm rounded-4 p-4 bg-white">
                <div class="table-responsive">
                    <table class="table align-middle table-hover small">
                        <thead class="text-secondary text-uppercase">
                            <tr>
                                <th>ID</th>
                                <th>Category Name</th>
                                <th>Description</th>
                                <th>Status</th>
                                <th class="text-end">Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${categories}" var="cat">
                                <tr>
                                    <td class="fw-bold">${cat.categoryId}</td>
                                    <td class="fw-semibold text-dark">${cat.name}</td>
                                    <td class="text-secondary">${cat.description}</td>
                                    <td>
                                        <span class="badge ${cat.status == 'ACTIVE' ? 'bg-success' : 'bg-secondary'} rounded-pill">
                                            ${cat.status}
                                        </span>
                                    </td>
                                    <td class="text-end">
                                        <div class="d-flex justify-content-end gap-2">
                                            <button type="button" class="btn btn-sm btn-outline-primary py-0 px-2"
                                                    onclick='editCat(${cat.categoryId}, "${cat.name}", "${cat.status}", `${cat.description}`)'
                                                    data-bs-toggle="modal" data-bs-target="#categoryModal">
                                                <i class="bi bi-pencil-square"></i>
                                            </button>
                                            <form action="${pageContext.request.contextPath}/admin/categories/delete" method="POST"
                                                  onsubmit="return confirm('Delete this category? (Will fail if products are linked)');">
                                                <input type="hidden" name="categoryId" value="${cat.categoryId}">
                                                <button type="submit" class="btn btn-sm btn-outline-danger py-0 px-2">
                                                    <i class="bi bi-trash"></i>
                                                </button>
                                            </form>
                                        </div>
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

<!-- Category Modal -->
<div class="modal fade" id="categoryModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content border-0 rounded-4 shadow">
            <div class="modal-header border-0 pb-0">
                <h5 class="modal-title fw-bold" id="catModalTitle">Category Details</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <form action="${pageContext.request.contextPath}/admin/categories/save" method="POST">
                <div class="modal-body">
                    <input type="hidden" name="categoryId" id="modalCatId" value="">

                    <div class="mb-3">
                        <label class="form-label small fw-semibold">Category Name</label>
                        <input type="text" name="name" id="modalCatName" class="form-control" required>
                    </div>

                    <div class="mb-3">
                        <label class="form-label small fw-semibold">Status</label>
                        <select name="status" id="modalCatStatus" class="form-select">
                            <option value="ACTIVE">ACTIVE</option>
                            <option value="INACTIVE">INACTIVE</option>
                        </select>
                    </div>

                    <div class="mb-3">
                        <label class="form-label small fw-semibold">Description</label>
                        <textarea name="description" id="modalCatDesc" class="form-control" rows="3"></textarea>
                    </div>
                </div>
                <div class="modal-footer border-0 pt-0">
                    <button type="button" class="btn btn-secondary btn-sm" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-ss-primary btn-sm px-4">Save Category</button>
                </div>
            </form>
        </div>
    </div>
</div>

<script>
function clearCatModal() {
    document.getElementById('modalCatId').value = '';
    document.getElementById('modalCatName').value = '';
    document.getElementById('modalCatStatus').value = 'ACTIVE';
    document.getElementById('modalCatDesc').value = '';
    document.getElementById('catModalTitle').innerText = 'Add Category';
}

function editCat(id, name, status, desc) {
    document.getElementById('modalCatId').value = id;
    document.getElementById('modalCatName').value = name;
    document.getElementById('modalCatStatus').value = status;
    document.getElementById('modalCatDesc').value = desc;
    document.getElementById('catModalTitle').innerText = 'Edit Category #' + id;
}
</script>

<jsp:include page="/fragments/footer.jsp"/>
