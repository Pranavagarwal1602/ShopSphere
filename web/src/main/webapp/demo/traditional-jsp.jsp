<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.Date, java.text.SimpleDateFormat" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:set var="pageTitle" value="Traditional JSP Elements vs Modern EL | RTU Syllabus" scope="request"/>
<jsp:include page="/fragments/header.jsp"/>
<jsp:include page="/fragments/navbar.jsp"/>

<main class="py-4">
    <div class="container">
        <div class="p-4 bg-primary-subtle text-primary rounded-4 mb-4">
            <h4 class="fw-bold mb-1"><i class="bi bi-mortarboard-fill me-2"></i>RTU Advanced Java Syllabus: Traditional JSP Syntax Elements</h4>
            <p class="mb-0 text-secondary">
                Demonstrating the four fundamental JSP constructs taught in the university curriculum:
                <strong>Declarations</strong> (<code>&lt;%! %&gt;</code>), <strong>Scriptlets</strong> (<code>&lt;% %&gt;</code>), 
                <strong>Expressions</strong> (<code>&lt;%= %&gt;</code>), and <strong>JSP Comments</strong> (<code>&lt;%-- --%&gt;</code>).
            </p>
        </div>

        <%-- 1. JSP Declaration: Declaring class-level fields and methods within generated Servlet --%>
        <%!
            // Method declared inside the generated Servlet class
            private int computeDiscountedPrice(int originalPrice, int percentOff) {
                return originalPrice - (originalPrice * percentOff / 100);
            }

            private String getAcademicSemester() {
                return "RTU B.Tech 6th/7th Semester - Advanced Java";
            }
        %>

        <div class="row g-4">
            <!-- Traditional Scriptlets -->
            <div class="col-lg-6">
                <div class="card border-0 shadow-sm rounded-4 p-4 bg-white h-100">
                    <h5 class="fw-bold mb-3"><i class="bi bi-code-slash text-primary me-2"></i>1. Traditional Scriptlet &amp; Expression Syntax</h5>

                    <!-- JSP Scriptlet -->
                    <%
                        // Local variables in _jspService()
                        String serverPlatform = System.getProperty("os.name");
                        String javaRuntime = System.getProperty("java.version");
                        Date currentDate = new Date();
                        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy, hh:mm:ss a");
                        int sampleBasePrice = 5000;
                        int sampleDiscountRate = 20;
                        int finalSamplePrice = computeDiscountedPrice(sampleBasePrice, sampleDiscountRate);
                    %>

                    <table class="table table-bordered small">
                        <tr>
                            <th class="table-light" style="width: 45%;">JSP Declaration Method</th>
                            <td><code><%= getAcademicSemester() %></code></td>
                        </tr>
                        <tr>
                            <th class="table-light">Calculated via Scriptlet</th>
                            <td>Base: ₹<%= sampleBasePrice %> - <%= sampleDiscountRate %>% = <strong>₹<%= finalSamplePrice %></strong></td>
                        </tr>
                        <tr>
                            <th class="table-light">Server Timestamp</th>
                            <td><%= sdf.format(currentDate) %></td>
                        </tr>
                        <tr>
                            <th class="table-light">Host Operating System</th>
                            <td><%= serverPlatform %> (Java <%= javaRuntime %>)</td>
                        </tr>
                    </table>

                    <div class="p-3 bg-light rounded-3 small">
                        <strong>JSP Comment:</strong> <code>&lt;%-- JSP comments are stripped on the server and never sent to browser --%&gt;</code>
                    </div>
                </div>
            </div>

            <!-- Modern Expression Language (EL) & JSTL Contrast -->
            <div class="col-lg-6">
                <div class="card border-0 shadow-sm rounded-4 p-4 bg-white h-100">
                    <h5 class="fw-bold mb-3"><i class="bi bi-lightning-charge-fill text-warning me-2"></i>2. Modern Expression Language (EL) &amp; JSTL</h5>
                    <p class="small text-secondary">
                        Why modern Java enterprise development prefers EL over scriptlets:
                        Cleaner syntax, automatic null-safety, automatic HTML escaping, and complete separation of presentation from business logic.
                    </p>

                    <c:set var="studentCourse" value="RTU Advanced Java Capstone"/>
                    <c:set var="sampleCartTotal" value="${5000 * 0.8}"/>

                    <table class="table table-bordered small">
                        <tr>
                            <th class="table-light" style="width: 45%;">EL Implicit Scope</th>
                            <td><code>\${studentCourse}</code> &rarr; <strong>${studentCourse}</strong></td>
                        </tr>
                        <tr>
                            <th class="table-light">EL Arithmetic Evaluation</th>
                            <td><code>\${5000 * 0.8}</code> &rarr; <strong>₹${sampleCartTotal}</strong></td>
                        </tr>
                        <tr>
                            <th class="table-light">Null Safety Handling</th>
                            <td><code>\${not empty nonExistentVar ? nonExistentVar : 'Safe Fallback'}</code> &rarr; <em>${not empty nonExistentVar ? nonExistentVar : 'Safe Fallback'}</em></td>
                        </tr>
                    </table>

                    <div class="alert alert-success small mb-0">
                        <i class="bi bi-check2-circle me-1"></i> ShopSphere's main customer and admin pages utilize pure EL and JSTL tags for MVC clean code standards.
                    </div>
                </div>
            </div>
        </div>
    </div>
</main>

<jsp:include page="/fragments/footer.jsp"/>
