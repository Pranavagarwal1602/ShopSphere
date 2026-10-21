<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="pageTitle" value="Product Management | Admin" scope="request"/>
<jsp:include page="/fragments/header.jsp"/>
<jsp:include page="/fragments/navbar.jsp"/>

<div class="container-fluid p-0">
    <div class="row g-0">
        <jsp:include page="/fragments/sidebar.jsp">
            <jsp:param name="activeNav" value="products"/>
        </jsp:include>

        <div class="col-lg-9 col-md-8 p-4">
            <div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-2">
                <div>
                    <h3 class="fw-bold mb-1">Product Management</h3>
                    <p class="text-secondary small mb-0">Create, edit, adjust prices, stock levels, and product visibility</p>
                </div>
                <button type="button" class="btn btn-ss-primary btn-sm rounded-pill px-3" data-bs-toggle="modal" data-bs-target="#productModal" onclick="clearProductModal()">
                    <i class="bi bi-plus-lg me-1"></i> Add New Product
                </button>
            </div>

            <!-- Filter Search Bar -->
            <div class="card border-0 shadow-sm rounded-4 p-3 mb-4 bg-white">
                <form action="${pageContext.request.contextPath}/admin/products" method="GET" class="row g-2">
                    <div class="col-md-6">
                        <input type="text" name="q" class="form-control form-control-sm" placeholder="Search by product title or brand..." value="${searchKeyword}">
                    </div>
                    <div class="col-md-4">
                        <select name="category" class="form-select form-select-sm">
                            <option value="">All Categories</option>
                            <c:forEach items="${categories}" var="c">
                                <option value="${c.categoryId}" ${selectedCategory == c.categoryId ? 'selected' : ''}>${c.name}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="col-md-2 d-flex gap-2">
                        <button type="submit" class="btn btn-primary btn-sm w-100">Filter</button>
                        <a href="${pageContext.request.contextPath}/admin/products" class="btn btn-outline-secondary btn-sm">Reset</a>
                    </div>
                </form>
            </div>

            <!-- Products Table -->
            <div class="card border-0 shadow-sm rounded-4 p-4 bg-white">
                <div class="table-responsive">
                    <table class="table align-middle table-hover small">
                        <thead class="text-secondary text-uppercase">
                            <tr>
                                <th>ID</th>
                                <th>Product</th>
                                <th>Category</th>
                                <th>Price</th>
                                <th>Discount</th>
                                <th>Stock</th>
                                <th>Status</th>
                                <th class="text-end">Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${products}" var="p">
                                <tr>
                                    <td class="fw-bold">${p.productId}</td>
                                    <td>
                                        <div class="d-flex align-items-center gap-2">
                                            <img src="${p.imageUrl}" alt="${p.name}" class="rounded border" style="width: 40px; height: 40px; object-fit: contain;">
                                            <div>
                                                <span class="fw-semibold text-dark"><c:out value="${p.name}"/></span>
                                                <div class="text-muted" style="font-size: 0.75rem;">${p.brand}</div>
                                            </div>
                                        </div>
                                    </td>
                                    <td>${p.categoryName}</td>
                                    <td class="fw-bold">₹<fmt:formatNumber value="${p.price}" minFractionDigits="2"/></td>
                                    <td class="text-danger">₹<fmt:formatNumber value="${p.discount}" minFractionDigits="2"/></td>
                                    <td>
                                        <span class="badge ${p.stock <= 10 ? 'bg-danger' : 'bg-success'} rounded-pill">
                                            ${p.stock}
                                        </span>
                                    </td>
                                    <td>
                                        <span class="badge ${p.status == 'ACTIVE' ? 'bg-success-subtle text-success' : 'bg-secondary-subtle text-secondary'} rounded-pill">
                                            ${p.status}
                                        </span>
                                    </td>
                                    <td class="text-end">
                                        <button type="button" class="btn btn-sm btn-outline-primary py-0 px-2"
                                                onclick='editProduct(${p.productId}, ${p.categoryId}, "${p.name}", "${p.brand}", "${p.price}", "${p.discount}", ${p.stock}, "${p.imageUrl}", "${p.status}", `${p.description}`)'
                                                data-bs-toggle="modal" data-bs-target="#productModal">
                                            <i class="bi bi-pencil-square"></i>
                                        </button>
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

<!-- Product Add/Edit Modal -->
<div class="modal fade" id="productModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content border-0 rounded-4 shadow">
            <div class="modal-header border-0 pb-0">
                <h5 class="modal-title fw-bold" id="productModalTitle">Product Details</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <form action="${pageContext.request.contextPath}/admin/products/save" method="POST">
                <div class="modal-body">
                    <input type="hidden" name="productId" id="modalProductId" value="">

                    <div class="row g-3 mb-3">
                        <div class="col-md-8">
                            <label class="form-label small fw-semibold">Product Name</label>
                            <input type="text" name="name" id="modalName" class="form-control" required>
                        </div>
                        <div class="col-md-4">
                            <label class="form-label small fw-semibold">Brand</label>
                            <input type="text" name="brand" id="modalBrand" class="form-control" required>
                        </div>
                    </div>

                    <div class="row g-3 mb-3">
                        <div class="col-md-6">
                            <label class="form-label small fw-semibold">Category</label>
                            <select name="categoryId" id="modalCategory" class="form-select" required>
                                <c:forEach items="${categories}" var="cat">
                                    <option value="${cat.categoryId}">${cat.name}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-md-3">
                            <label class="form-label small fw-semibold">Price (₹)</label>
                            <input type="number" step="0.01" name="price" id="modalPrice" class="form-control" required>
                        </div>
                        <div class="col-md-3">
                            <label class="form-label small fw-semibold">Discount (₹)</label>
                            <input type="number" step="0.01" name="discount" id="modalDiscount" class="form-control" value="0.00" required>
                        </div>
                    </div>

                    <div class="row g-3 mb-3">
                        <div class="col-md-4">
                            <label class="form-label small fw-semibold">Stock Quantity</label>
                            <input type="number" name="stock" id="modalStock" class="form-control" value="10" required>
                        </div>
                        <div class="col-md-4">
                            <label class="form-label small fw-semibold">Status</label>
                            <select name="status" id="modalStatus" class="form-select">
                                <option value="ACTIVE">ACTIVE</option>
                                <option value="INACTIVE">INACTIVE</option>
                            </select>
                        </div>
                        <div class="col-md-4">
                            <label class="form-label small fw-semibold">Image URL</label>
                            <input type="url" name="imageUrl" id="modalImageUrl" class="form-control" placeholder="https://...">
                        </div>
                    </div>

                    <div class="mb-3">
                        <label class="form-label small fw-semibold">Description</label>
                        <textarea name="description" id="modalDesc" class="form-control" rows="3" required></textarea>
                    </div>
                </div>
                <div class="modal-footer border-0 pt-0">
                    <button type="button" class="btn btn-secondary btn-sm" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-ss-primary btn-sm px-4">Save Product</button>
                </div>
            </form>
        </div>
    </div>
</div>

<script>
function clearProductModal() {
    document.getElementById('modalProductId').value = '';
    document.getElementById('modalName').value = '';
    document.getElementById('modalBrand').value = '';
    document.getElementById('modalPrice').value = '';
    document.getElementById('modalDiscount').value = '0.00';
    document.getElementById('modalStock').value = '10';
    document.getElementById('modalStatus').value = 'ACTIVE';
    document.getElementById('modalImageUrl').value = '';
    document.getElementById('modalDesc').value = '';
    document.getElementById('productModalTitle').innerText = 'Add New Product';
}

function editProduct(id, catId, name, brand, price, discount, stock, img, status, desc) {
    document.getElementById('modalProductId').value = id;
    document.getElementById('modalName').value = name;
    document.getElementById('modalBrand').value = brand;
    document.getElementById('modalCategory').value = catId;
    document.getElementById('modalPrice').value = price;
    document.getElementById('modalDiscount').value = discount;
    document.getElementById('modalStock').value = stock;
    document.getElementById('modalStatus').value = status;
    document.getElementById('modalImageUrl').value = img;
    document.getElementById('modalDesc').value = desc;
    document.getElementById('productModalTitle').innerText = 'Edit Product #' + id;
}
</script>

<jsp:include page="/fragments/footer.jsp"/>
