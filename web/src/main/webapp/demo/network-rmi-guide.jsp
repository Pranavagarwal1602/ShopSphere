<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:set var="pageTitle" value="Network Sockets & RMI Architecture | RTU Syllabus" scope="request"/>
<jsp:include page="/fragments/header.jsp"/>
<jsp:include page="/fragments/navbar.jsp"/>

<main class="py-4">
    <div class="container">
        <div class="p-4 bg-primary-subtle text-primary rounded-4 mb-4">
            <h4 class="fw-bold mb-1"><i class="bi bi-hdd-network me-2"></i>RTU Advanced Java Syllabus: Distributed Computing &amp; Networking</h4>
            <p class="mb-0 text-secondary">
                ShopSphere includes two independently compiled, runnable standalone submodules:
                <code>shopsphere-network</code> (Java <code>ServerSocket</code> / <code>Socket</code>) and 
                <code>shopsphere-rmi</code> (Java RMI Remote Method Invocation &amp; RMI Registry).
            </p>
        </div>

        <div class="row g-4">
            <!-- Socket Architecture -->
            <div class="col-lg-6">
                <div class="card border-0 shadow-sm rounded-4 p-4 bg-white h-100">
                    <div class="d-flex align-items-center gap-2 mb-3">
                        <i class="bi bi-cpu fs-4 text-primary"></i>
                        <h5 class="fw-bold mb-0">1. Socket Inventory Protocol (Port 8888)</h5>
                    </div>
                    <p class="small text-secondary">
                        Built with <code>java.net.ServerSocket</code> and multi-threaded worker pools. Allows desktop warehouse barcode scanners to query live database inventory.
                    </p>

                    <h6 class="fw-bold small text-uppercase text-secondary">Supported Wire Protocol Commands:</h6>
                    <ul class="list-group list-group-flush small mb-3">
                        <li class="list-group-item px-0"><code>GET_PRODUCT &lt;id&gt;</code> &rarr; Returns serialized/JSON Product object</li>
                        <li class="list-group-item px-0"><code>GET_STOCK &lt;id&gt;</code> &rarr; Returns real-time integer stock quantity</li>
                        <li class="list-group-item px-0"><code>SEARCH &lt;query&gt;</code> &rarr; Returns matching product catalogue array</li>
                        <li class="list-group-item px-0"><code>QUIT</code> &rarr; Graceful TCP socket disconnect</li>
                    </ul>

                    <h6 class="fw-bold small text-uppercase text-secondary">Running the Server &amp; Client:</h6>
                    <pre class="bg-dark text-light p-3 rounded-3 small mb-0"><code># 1. Start Socket Server Daemon:
mvn -pl network exec:java -Dexec.mainClass="com.shopsphere.network.InventorySocketServer"

# 2. In another terminal, run Client Tester:
mvn -pl network exec:java -Dexec.mainClass="com.shopsphere.network.InventorySocketClient"</code></pre>
                </div>
            </div>

            <!-- RMI Architecture -->
            <div class="col-lg-6">
                <div class="card border-0 shadow-sm rounded-4 p-4 bg-white h-100">
                    <div class="d-flex align-items-center gap-2 mb-3">
                        <i class="bi bi-diagram-3 fs-4 text-success"></i>
                        <h5 class="fw-bold mb-0">2. Java RMI &amp; RMI Registry (Port 1099)</h5>
                    </div>
                    <p class="small text-secondary">
                        Distributed enterprise service using <code>java.rmi.Remote</code> and <code>UnicastRemoteObject</code>. 
                        Binds an <code>InventoryService</code> remote proxy into the local RMI Registry.
                    </p>

                    <h6 class="fw-bold small text-uppercase text-secondary">Remote Interface Contract:</h6>
                    <pre class="bg-light p-2 rounded small"><code>public interface InventoryService extends Remote {
    Product getProduct(int id) throws RemoteException;
    int getStock(int productId) throws RemoteException;
    boolean updateStock(int productId, int quantity) throws RemoteException;
}</code></pre>

                    <h6 class="fw-bold small text-uppercase text-secondary">Running RMI Server &amp; Client:</h6>
                    <pre class="bg-dark text-light p-3 rounded-3 small mb-0"><code># 1. Start RMI Server (launches registry on 1099):
mvn -pl rmi exec:java -Dexec.mainClass="com.shopsphere.rmi.RmiServerMain"

# 2. In another terminal, run Remote Client:
mvn -pl rmi exec:java -Dexec.mainClass="com.shopsphere.rmi.RmiClientMain"</code></pre>
                </div>
            </div>
        </div>
    </div>
</main>

<jsp:include page="/fragments/footer.jsp"/>
