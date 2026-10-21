<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:set var="pageTitle" value="Applet Lifecycle & Evolution | RTU Syllabus" scope="request"/>
<jsp:include page="/fragments/header.jsp"/>
<jsp:include page="/fragments/navbar.jsp"/>

<main class="py-4">
    <div class="container">
        <div class="p-4 bg-primary-subtle text-primary rounded-4 mb-4">
            <h4 class="fw-bold mb-1"><i class="bi bi-clock-history me-2"></i>RTU Advanced Java Syllabus: Java Applet Lifecycle &amp; Historical Context</h4>
            <p class="mb-0 text-secondary">
                <strong>Academic Requirement:</strong> While Applets were an integral part of historical Java (JDK 1.0 to Java 8),
                NPAPI plugin support was removed from all modern web browsers (Chrome, Edge, Firefox) and the <code>java.applet.Applet</code> 
                class was deprecated in Java 9 and permanently removed in Java 17+. This page provides the required theoretical and lifecycle analysis for university examinations.
            </p>
        </div>

        <div class="row g-4">
            <!-- Applet Lifecycle -->
            <div class="col-lg-6">
                <div class="card border-0 shadow-sm rounded-4 p-4 bg-white h-100">
                    <h5 class="fw-bold mb-3"><i class="bi bi-arrow-repeat text-primary me-2"></i>1. Historical Applet Lifecycle Methods</h5>
                    <div class="list-group list-group-flush small">
                        <div class="list-group-item px-0">
                            <strong><code>init()</code>:</strong> Called exactly once by the browser or Applet Viewer when the applet is first loaded into memory. Used to initialize variables, download media assets, and construct AWT/Swing GUI components.
                        </div>
                        <div class="list-group-item px-0">
                            <strong><code>start()</code>:</strong> Invoked after <code>init()</code>, and every time the user navigates back to the webpage containing the applet. Resumes background animation threads and audio playback.
                        </div>
                        <div class="list-group-item px-0">
                            <strong><code>paint(Graphics g)</code>:</strong> Inherited from <code>java.awt.Component</code>. Invoked whenever the applet window requires redrawing or re-rendering visual graphics.
                        </div>
                        <div class="list-group-item px-0">
                            <strong><code>stop()</code>:</strong> Triggered whenever the user scrolls away, minimizes, or navigates to another URL. Suspends resource-heavy loops and threads.
                        </div>
                        <div class="list-group-item px-0">
                            <strong><code>destroy()</code>:</strong> Invoked once before the applet is evicted from memory and garbage collected. Performs clean resource deallocation.
                        </div>
                    </div>
                </div>
            </div>

            <!-- Modern Transition -->
            <div class="col-lg-6">
                <div class="card border-0 shadow-sm rounded-4 p-4 bg-white h-100">
                    <h5 class="fw-bold mb-3"><i class="bi bi-shield-check text-success me-2"></i>2. Servlet Lifecycle vs. Applet Lifecycle</h5>
                    <p class="small text-secondary">
                        How Java enterprise evolved from client-side Applet sandboxes to server-side Jakarta Servlets:
                    </p>

                    <table class="table table-bordered small">
                        <thead class="table-light">
                            <tr>
                                <th>Dimension</th>
                                <th>Java Applet (Historical)</th>
                                <th>Jakarta Servlet 6.1 (ShopSphere)</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr>
                                <td><strong>Execution</strong></td>
                                <td>Client browser JVM via browser plugin</td>
                                <td>Server container (Apache Tomcat 11)</td>
                            </tr>
                            <tr>
                                <td><strong>Lifecycle</strong></td>
                                <td><code>init &rarr; start &rarr; stop &rarr; destroy</code></td>
                                <td><code>init &rarr; service (doGet/doPost) &rarr; destroy</code></td>
                            </tr>
                            <tr>
                                <td><strong>Security</strong></td>
                                <td>Client security sandbox (vulnerable)</td>
                                <td>Server-side filters, BCrypt, and SQL PreparedStatements</td>
                            </tr>
                            <tr>
                                <td><strong>Presentation</strong></td>
                                <td>AWT / Swing pixel drawing</td>
                                <td>HTML5, Bootstrap 5, JSP &amp; Expression Language</td>
                            </tr>
                        </tbody>
                    </table>

                    <div class="alert alert-info small mb-0">
                        <i class="bi bi-lightbulb-fill me-1"></i> <strong>Exam Viva Tip:</strong> In viva examinations, emphasize that Servlets replaced Applets because enterprise web applications require centralized database connections, secure transaction handling, and zero client-side installation requirements.
                    </div>
                </div>
            </div>
        </div>
    </div>
</main>

<jsp:include page="/fragments/footer.jsp"/>
