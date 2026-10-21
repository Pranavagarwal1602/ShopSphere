# ShopSphere Enterprise E-Commerce Suite

**An RTU Advanced Java Capstone Project**  
Full-Stack Java Enterprise Web Application, Desktop Administration, and Distributed Computing Suite.

---

## 1. Project Overview & Architecture

**ShopSphere** is a complete, production-grade Java enterprise web application and distributed computing suite designed to meet every syllabus component of the **RTU Advanced Java** curriculum.

### Multi-Tier MVC Architecture
```
Browser / HTTP Client  <-->  JSP 3.1 & Custom Tag Files (Presentation Tier)
                                     |
                             Jakarta Servlets 6.1 (Controller Tier)
                                     |
                             Java Service Layer (Business Logic & Transactions)
                                     |
                             JDBC DAO Layer (Data Access Tier)
                                     |
                              MySQL 8.0 Database (Persistence Tier)
```

### Modular Repository Structure
- **`shared/`**: Reusable domain POJOs (`User`, `Product`, `Category`, `Order`, `CartItem`, `Coupon`, `Review`, `Address`, `Payment`) and `InventoryDTO` implementing `java.io.Serializable`.
- **`web/`**: Jakarta Servlet 6.1 web application with modern JSP 3.1, Expression Language (EL), JSTL (Core, SQL, XML, Functions), custom tag files, filters, and listeners packaged as `shopsphere.war` for **Apache Tomcat 11**.
- **`swing-admin/`**: Desktop administration console utilizing Java Swing MVC (`JFrame`, `JTable`, `JTabbedPane`, `JOptionPane`) connecting directly to MySQL.
- **`network/`**: Standalone socket daemon using `java.net.ServerSocket` and multi-threaded barcode/warehouse client on TCP Port `8888`.
- **`rmi/`**: Distributed remote inventory service using `java.rmi.Remote`, `UnicastRemoteObject`, and RMI Registry on Port `1099`.
- **`database/`**: Complete database initialization script (`shopsphere.sql`).
- **`docs/`**: Comprehensive ER diagrams, system architecture diagrams, sequence diagrams, syllabus mappings, report outline, and viva Q&A.

---

## 2. Prerequisites & Technology Stack

| Component | Requirement / Version |
| :--- | :--- |
| **Java JDK** | OpenJDK 25 or another Java 25-compatible JDK (LTS) |
| **Servlet Container** | Apache Tomcat 11.0.x (Jakarta EE 10 / Servlet 6.1) |
| **Database** | MySQL Server 8.0+ or MariaDB 10.4+ |
| **Build Tool** | Apache Maven 3.8+ |
| **Frontend Framework** | Bootstrap 5.3.3, Bootstrap Icons, Vanilla CSS |

---

## 3. Database Initialization (MySQL)

1. Open your MySQL client (Command Line, MySQL Workbench, or phpMyAdmin):
   ```bash
   mysql -u root -p
   ```
2. Execute the complete initialization script:
   ```sql
   SOURCE c:/Users/lovis/Desktop/Pranav/database/shopsphere.sql;
   ```
   *This creates the `shopsphere` database, all 12 normalized relational tables, foreign key constraints, indexes, sample categories, 12 products with images, coupons, and verified user accounts.*

### Pre-Configured Test Credentials:
- **Administrator Portal:** `admin@shopsphere.com` / `Admin@123`
- **Customer Storefront:** `customer@shopsphere.com` / `Customer@123`
- *Both passwords are encrypted using verified BCrypt hashes (`$2a$10$...`).*

---

## 4. Configuration & Building

### Database Credentials
Database connection settings are configured in `web/src/main/resources/db.properties`.  
You can also override them via standard environment variables:
```bash
set DB_URL=jdbc:mysql://localhost:3306/shopsphere?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
set DB_USER=root
set DB_PASSWORD=root
```

### Compiling and Packaging
Build all 5 modules from the root directory:
```bash
mvn clean install
```
This produces:
- `web/target/shopsphere.war`
- `swing-admin/target/shopsphere-swing-admin-1.0.0-SNAPSHOT.jar`
- `network/target/shopsphere-network-1.0.0-SNAPSHOT.jar`
- `rmi/target/shopsphere-rmi-1.0.0-SNAPSHOT.jar`

---

## 5. Deployment Instructions

### A. Deploying Web Application to Apache Tomcat 11
1. Download and extract **Apache Tomcat 11** to your system (e.g., `C:\apache-tomcat-11.0.5`).
2. Copy the generated `web/target/shopsphere.war` file into Tomcat's `webapps/` folder:
   ```bash
   copy web\target\shopsphere.war C:\apache-tomcat-11.0.5\webapps\
   ```
3. Start Tomcat:
   ```bash
   C:\apache-tomcat-11.0.5\bin\startup.bat
   ```
4. Access the web storefront in any browser:
   - **Storefront Home:** [http://localhost:8080/shopsphere/](http://localhost:8080/shopsphere/)
   - **Admin Dashboard:** [http://localhost:8080/shopsphere/admin/dashboard](http://localhost:8080/shopsphere/admin/dashboard)
   - **RTU Syllabus Demonstration Lab:** [http://localhost:8080/shopsphere/demo/jstl-sql.jsp](http://localhost:8080/shopsphere/demo/jstl-sql.jsp)

---

## 6. Running Distributed Modules

### B. Running Java Swing Desktop Administration
Execute the standalone desktop JAR:
```bash
java -jar swing-admin/target/shopsphere-swing-admin-1.0.0-SNAPSHOT.jar
```
*(Or run via Maven: `mvn -pl swing-admin exec:java -Dexec.mainClass="com.shopsphere.swing.SwingAdminMain"`)*

### C. Running Java Socket Inventory Daemon & Client
1. **Start Server Daemon (Port 8888):**
   ```bash
   mvn -pl network exec:java -Dexec.mainClass="com.shopsphere.network.InventorySocketServer"
   ```
2. **In a second terminal, execute the Client:**
   ```bash
   mvn -pl network exec:java -Dexec.mainClass="com.shopsphere.network.InventorySocketClient"
   ```

### D. Running Java RMI Server & Remote Client
1. **Start RMI Server & Registry (Port 1099):**
   ```bash
   mvn -pl rmi exec:java -Dexec.mainClass="com.shopsphere.rmi.RmiServerMain"
   ```
2. **In a second terminal, execute Remote Client Invocations:**
   ```bash
   mvn -pl rmi exec:java -Dexec.mainClass="com.shopsphere.rmi.RmiClientMain"
   ```

---

## 7. RTU Advanced Java Syllabus Mapping

| RTU Syllabus Unit & Topic | Implementation in ShopSphere | Source File / Page |
| :--- | :--- | :--- |
| **Unit I: Java Swing & GUI** | Swing MVC Architecture, `JFrame`, `JTable`, `JTabbedPane`, `JOptionPane` | [SwingAdminMain.java](file:///c:/Users/lovis/Desktop/Pranav/swing-admin/src/main/java/com/shopsphere/swing/SwingAdminMain.java) |
| **Unit I: Applets Concepts** | Historical Applet Lifecycle (`init`, `start`, `stop`, `destroy`) vs Servlet comparison | [applet-history.jsp](file:///c:/Users/lovis/Desktop/Pranav/web/src/main/webapp/demo/applet-history.jsp) |
| **Unit II: JDBC Database Access** | MySQL Connector/J, `Connection`, `PreparedStatement`, Transactions, JNDI lookup | [DBConnectionUtil.java](file:///c:/Users/lovis/Desktop/Pranav/web/src/main/java/com/shopsphere/util/DBConnectionUtil.java) |
| **Unit III: Networking & Sockets** | Multi-threaded `ServerSocket`, client `Socket`, TCP wire protocol | [InventorySocketServer.java](file:///c:/Users/lovis/Desktop/Pranav/network/src/main/java/com/shopsphere/network/InventorySocketServer.java) |
| **Unit III: Serialization** | `Serializable` DTO object streaming across sockets (`ObjectOutputStream`) | [InventoryDTO.java](file:///c:/Users/lovis/Desktop/Pranav/shared/src/main/java/com/shopsphere/dto/InventoryDTO.java) |
| **Unit IV: Java RMI** | `Remote`, `RemoteException`, `UnicastRemoteObject`, RMI Registry (1099) | [RmiServerMain.java](file:///c:/Users/lovis/Desktop/Pranav/rmi/src/main/java/com/shopsphere/rmi/RmiServerMain.java) |
| **Unit V: Servlets Architecture** | Jakarta Servlet 6.1 (`doGet`, `doPost`, `ServletConfig`, `ServletContext`) | [LoginServlet.java](file:///c:/Users/lovis/Desktop/Pranav/web/src/main/java/com/shopsphere/controller/LoginServlet.java) |
| **Unit V: Filters & Security** | `AuthenticationFilter` & `AdminFilter` role authorization, CSRF protection | [AdminFilter.java](file:///c:/Users/lovis/Desktop/Pranav/web/src/main/java/com/shopsphere/filter/AdminFilter.java) |
| **Unit V: Listeners & Sessions** | `ServletContextListener`, `HttpSessionListener`, active session count | [AppContextListener.java](file:///c:/Users/lovis/Desktop/Pranav/web/src/main/java/com/shopsphere/listener/AppContextListener.java) |
| **Unit VI: JSP Architecture** | JSP 3.1 lifecycle, Declarations (`<%! %>`), Scriptlets (`<% %>`), Expressions | [traditional-jsp.jsp](file:///c:/Users/lovis/Desktop/Pranav/web/src/main/webapp/demo/traditional-jsp.jsp) |
| **Unit VI: JSTL & Custom Tags** | JSTL Core, JSTL SQL (`<sql:query>`), JSTL XML (`<x:parse>`), Custom Tag Files | [jstl-sql.jsp](file:///c:/Users/lovis/Desktop/Pranav/web/src/main/webapp/demo/jstl-sql.jsp), [productCard.tag](file:///c:/Users/lovis/Desktop/Pranav/web/src/main/webapp/WEB-INF/tags/productCard.tag) |
| **Unit VI: Internationalization** | Java `ResourceBundle` and Locale property files (English & Hindi) | [messages_en.properties](file:///c:/Users/lovis/Desktop/Pranav/web/src/main/resources/messages_en.properties) |
