<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="sql" uri="jakarta.tags.sql" %>
<%@ taglib prefix="x" uri="jakarta.tags.xml" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="pageTitle" value="JSTL SQL & XML Tags Demo | RTU Syllabus" scope="request"/>
<jsp:include page="/fragments/header.jsp"/>
<jsp:include page="/fragments/navbar.jsp"/>

<main class="py-4">
    <div class="container">
        <!-- Educational Callout -->
        <div class="p-4 bg-primary-subtle text-primary rounded-4 mb-4">
            <h4 class="fw-bold mb-1"><i class="bi bi-mortarboard-fill me-2"></i>RTU Advanced Java Syllabus: JSTL SQL &amp; XML Demonstration</h4>
            <p class="mb-0 text-secondary">
                <strong>Pedagogical Distinction:</strong> The core ShopSphere production architecture adheres strictly to
                <code>JSP &rarr; Servlet &rarr; Service &rarr; DAO &rarr; JDBC</code>. This page demonstrates traditional JSTL SQL (<code>&lt;sql:query&gt;</code>) 
                and JSTL XML (<code>&lt;x:parse&gt;</code>, <code>&lt;x:forEach&gt;</code>) tags specifically for viva practical demonstration.
            </p>
        </div>

        <div class="row g-4">
            <!-- JSTL SQL Section -->
            <div class="col-lg-6">
                <div class="card border-0 shadow-sm rounded-4 p-4 h-100 bg-white">
                    <h5 class="fw-bold mb-3"><i class="bi bi-database text-primary me-2"></i>1. JSTL SQL Tag Demo (&lt;sql:query&gt;)</h5>
                    <p class="small text-secondary">
                        Executing read-only SQL directly in JSP using JSTL SQL tags:
                    </p>

                    <!-- Attempting JSTL SQL Query -->
                    <c:catch var="sqlError">
                        <sql:setDataSource var="db" driver="com.mysql.cj.jdbc.Driver"
                                           url="jdbc:mysql://localhost:3306/shopsphere?useSSL=false&amp;allowPublicKeyRetrieval=true&amp;serverTimezone=UTC"
                                           user="root" password="root"/>

                        <sql:query dataSource="${db}" var="categoriesResult">
                            SELECT category_id, name, status FROM categories LIMIT 5;
                        </sql:query>

                        <div class="table-responsive">
                            <table class="table table-sm table-bordered small">
                                <thead class="table-light">
                                    <tr>
                                        <th>ID</th>
                                        <th>Category Name</th>
                                        <th>Status</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="row" items="${categoriesResult.rows}">
                                        <tr>
                                            <td><c:out value="${row.category_id}"/></td>
                                            <td><c:out value="${row.name}"/></td>
                                            <td><span class="badge bg-success"><c:out value="${row.status}"/></span></td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </c:catch>

                    <c:if test="${not empty sqlError}">
                        <div class="alert alert-secondary small mb-0">
                            <strong>Note:</strong> Direct JSTL SQL requires live MySQL on localhost:3306 with credentials root/root.
                            (In production, queries are safely executed in DAO prepared statements).
                        </div>
                    </c:if>
                </div>
            </div>

            <!-- JSTL XML Section -->
            <div class="col-lg-6">
                <div class="card border-0 shadow-sm rounded-4 p-4 h-100 bg-white">
                    <h5 class="fw-bold mb-3"><i class="bi bi-filetype-xml text-primary me-2"></i>2. JSTL XML Tag Demo (&lt;x:parse&gt;)</h5>
                    <p class="small text-secondary">
                        Parsing and transforming an XML inventory document using XPath and JSTL XML tags:
                    </p>

                    <c:set var="xmlCatalog">
                        <catalog>
                            <item id="101">
                                <title>Advanced Java Handbook</title>
                                <price>749.00</price>
                                <format>Paperback</format>
                            </item>
                            <item id="102">
                                <title>SoundMax ANC Headphones</title>
                                <price>4499.00</price>
                                <format>Hardware</format>
                            </item>
                            <item id="103">
                                <title>AMOLED Smartwatch Pro</title>
                                <price>2499.00</price>
                                <format>Wearable</format>
                            </item>
                        </catalog>
                    </c:set>

                    <x:parse xml="${xmlCatalog}" var="parsedDoc"/>

                    <div class="table-responsive">
                        <table class="table table-sm table-bordered small">
                            <thead class="table-light">
                                <tr>
                                    <th>Item Title</th>
                                    <th>Format</th>
                                    <th>Price</th>
                                </tr>
                            </thead>
                            <tbody>
                                <x:forEach select="$parsedDoc/catalog/item">
                                    <tr>
                                        <td><strong><x:out select="title"/></strong></td>
                                        <td><x:out select="format"/></td>
                                        <td class="text-primary fw-bold">₹<x:out select="price"/></td>
                                    </tr>
                                </x:forEach>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>
</main>

<jsp:include page="/fragments/footer.jsp"/>
