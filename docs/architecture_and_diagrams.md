# ShopSphere Architecture & Design Diagrams

## 1. Relational Database Entity-Relationship (ER) Diagram

```mermaid
erDiagram
    USERS ||--o{ ADDRESSES : "has multiple"
    USERS ||--o{ ORDERS : "places"
    USERS ||--o{ CART : "owns items in"
    USERS ||--o{ WISHLIST : "saves items to"
    USERS ||--o{ REVIEWS : "writes"
    USERS ||--o{ COUPON_USAGE : "redeems"

    CATEGORIES ||--o{ PRODUCTS : "contains"
    PRODUCTS ||--o{ CART : "referenced in"
    PRODUCTS ||--o{ WISHLIST : "saved in"
    PRODUCTS ||--o{ ORDER_ITEMS : "purchased as"
    PRODUCTS ||--o{ REVIEWS : "receives"

    ORDERS ||--o{ ORDER_ITEMS : "consists of"
    ORDERS ||--|| PAYMENTS : "billed via"
    ORDERS ||--o{ COUPON_USAGE : "records"
    ADDRESSES ||--o{ ORDERS : "delivers to"
    COUPONS ||--o{ COUPON_USAGE : "tracked by"

    USERS {
        int user_id PK
        string name
        string email UK
        string password
        string mobile
        string role
        string status
        timestamp created_at
    }

    ADDRESSES {
        int address_id PK
        int user_id FK
        string address_line
        string city
        string state
        string pincode
        string address_type
        boolean is_default
    }

    CATEGORIES {
        int category_id PK
        string name UK
        string description
        string status
    }

    PRODUCTS {
        int product_id PK
        int category_id FK
        string name
        string brand
        decimal price
        decimal discount
        int stock
        string image_url
        string status
    }

    CART {
        int cart_id PK
        int user_id FK
        int product_id FK
        int quantity
    }

    WISHLIST {
        int wishlist_id PK
        int user_id FK
        int product_id FK
    }

    ORDERS {
        int order_id PK
        int user_id FK
        int address_id FK
        decimal total_amount
        decimal discount
        string payment_method
        string payment_status
        string order_status
        timestamp created_at
    }

    ORDER_ITEMS {
        int order_item_id PK
        int order_id FK
        int product_id FK
        int quantity
        decimal price
    }

    PAYMENTS {
        int payment_id PK
        int order_id FK
        string payment_method
        string transaction_reference
        decimal amount
        string payment_status
    }

    COUPONS {
        int coupon_id PK
        string code UK
        string discount_type
        decimal discount_value
        decimal minimum_order
        decimal maximum_discount
        date expiry_date
        string status
    }

    COUPON_USAGE {
        int usage_id PK
        int coupon_id FK
        int user_id FK
        int order_id FK
        timestamp used_at
    }

    REVIEWS {
        int review_id PK
        int user_id FK
        int product_id FK
        int rating
        string review_text
    }
```

---

## 2. N-Tier System Architecture Diagram

```mermaid
graph TD
    subgraph Client Tier
        Browser[Modern Web Browser Chrome/Edge/Firefox]
        SwingApp[Java Swing Desktop Admin GUI]
        BarcodeClient[Network Warehouse Barcode Scanner]
        RmiRemoteClient[RMI Remote Client Application]
    end

    subgraph Web Presentation Tier [Apache Tomcat 11 Container]
        JSP[JSP 3.1 & Custom Tag Files productCard.tag]
        Filters[Security Filters: AuthenticationFilter & AdminFilter]
        Controllers[Jakarta Servlets 6.1: Login, Product, Cart, Checkout, Order]
        Listeners[AppContextListener: Lifecycle & Session Counter]
    end

    subgraph Business Logic & Networking Tier
        Services[Service Layer: UserService, CheckoutService, OrderService, ProductService]
        SocketServer[InventorySocketServer: java.net.ServerSocket Port 8888]
        RmiRegistry[RMI Registry & UnicastRemoteObject Port 1099]
    end

    subgraph Data Access & Persistence Tier
        DAO[JDBC DAO Layer: PreparedStatements & Transactions]
        Pool[Connection Manager & JNDI DataSource]
        MySQL[(MySQL 8.0 Database: shopsphere)]
    end

    Browser <-->|HTTP / HTTPS| Filters
    Filters <--> Controllers
    Controllers <--> JSP
    Controllers <--> Services
    SwingApp <-->|Direct JDBC| DAO
    BarcodeClient <-->|TCP Socket Stream| SocketServer
    RmiRemoteClient <-->|Java RMI Protocol| RmiRegistry

    SocketServer <--> DAO
    RmiRegistry <--> DAO
    Services <--> DAO
    DAO <--> Pool
    Pool <-->|JDBC Driver 9.1| MySQL
```

---

## 3. Order Placement & ACID Transaction Sequence Diagram

```mermaid
sequenceDiagram
    autonumber
    actor Customer as Customer (Browser)
    participant CheckoutServlet as CheckoutServlet
    participant CheckoutService as CheckoutService
    participant DBConn as DBConnectionUtil (ACID Tx)
    participant ProductDAO as ProductDAO (Stock Update)
    participant OrderDAO as OrderDAO
    participant PaymentDAO as PaymentDAO
    participant CartDAO as CartDAO

    Customer->>CheckoutServlet: POST /checkout/place-order (Address, Payment, Coupon)
    CheckoutServlet->>CheckoutServlet: Validate CSRF Token & Input Parameters
    CheckoutServlet->>CheckoutService: processOrder(userId, addressId, paymentMethod, coupon)

    CheckoutService->>DBConn: getConnection() -> setAutoCommit(false) [BEGIN TX]
    
    CheckoutService->>OrderDAO: insertOrder(conn, orderRecord)
    OrderDAO-->>CheckoutService: Generated orderId

    loop For each item in Cart
        CheckoutService->>OrderDAO: insertOrderItem(conn, item)
        CheckoutService->>ProductDAO: updateStockDelta(conn, productId, -quantity)
        alt Insufficient Stock Detected
            ProductDAO-->>CheckoutService: Stock Update Failed
            CheckoutService->>DBConn: rollback()
            CheckoutService-->>CheckoutServlet: Error "Insufficient stock"
            CheckoutServlet-->>Customer: Display Stock Conflict Error
        end
    end

    CheckoutService->>PaymentDAO: insertPayment(conn, paymentRecord)
    CheckoutService->>CartDAO: clearCart(conn, userId)

    CheckoutService->>DBConn: commit() [TX SUCCESSFUL]
    DBConn-->>CheckoutService: Transaction Committed
    CheckoutService-->>CheckoutServlet: CheckoutResult(Success, orderId)
    CheckoutServlet-->>Customer: Redirect to /order-success?orderId=...
```

---

## 4. User Authentication & Authorization Sequence Diagram

```mermaid
sequenceDiagram
    autonumber
    actor User as User / Admin
    participant LoginServlet as LoginServlet
    participant UserService as UserService
    participant PasswordUtil as PasswordUtil (BCrypt)
    participant UserDAO as UserDAO
    participant Session as HttpSession

    User->>LoginServlet: POST /login (email, password, csrfToken)
    LoginServlet->>LoginServlet: Verify CSRF Token match
    LoginServlet->>UserService: authenticate(email, plainPassword)
    UserService->>UserDAO: findByEmail(email)
    UserDAO-->>UserService: User Record with BCrypt Hash
    UserService->>PasswordUtil: checkPassword(plain, hashedPassword)
    PasswordUtil-->>UserService: Match confirmed (true)
    UserService-->>LoginServlet: Authenticated User Object

    LoginServlet->>Session: invalidate() (Fixation Protection)
    LoginServlet->>Session: createNewSession()
    LoginServlet->>Session: setAttribute("currentUser", User)
    
    alt Role == ADMIN
        LoginServlet-->>User: Redirect /admin/dashboard
    else Role == CUSTOMER
        LoginServlet-->>User: Redirect /home
    end
```
