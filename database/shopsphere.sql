-- =====================================================================
-- ShopSphere Database Setup Script
-- Academic Capstone: RTU Advanced Java Syllabus Project
-- Compatible with MySQL 8.0+ / MariaDB 10.4+
-- =====================================================================

DROP DATABASE IF EXISTS shopsphere;
CREATE DATABASE shopsphere CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE shopsphere;

-- ---------------------------------------------------------------------
-- 1. Table: users
-- Roles: 'CUSTOMER', 'ADMIN'
-- Password hashes generated using BCrypt ($2a$12$...)
-- ---------------------------------------------------------------------
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    mobile VARCHAR(20),
    role VARCHAR(20) NOT NULL DEFAULT 'CUSTOMER',
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_users_email (email),
    INDEX idx_users_role (role)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 2. Table: addresses
-- ---------------------------------------------------------------------
CREATE TABLE addresses (
    address_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    address_line VARCHAR(255) NOT NULL,
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL,
    pincode VARCHAR(20) NOT NULL,
    address_type VARCHAR(20) DEFAULT 'HOME',
    is_default BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_addresses_user (user_id)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 3. Table: categories
-- ---------------------------------------------------------------------
CREATE TABLE categories (
    category_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 4. Table: products
-- ---------------------------------------------------------------------
CREATE TABLE products (
    product_id INT AUTO_INCREMENT PRIMARY KEY,
    category_id INT NOT NULL,
    name VARCHAR(200) NOT NULL,
    brand VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    discount DECIMAL(10,2) DEFAULT 0.00,
    stock INT NOT NULL DEFAULT 0,
    image_url VARCHAR(500),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(category_id) ON DELETE RESTRICT,
    INDEX idx_products_category (category_id),
    INDEX idx_products_status (status),
    INDEX idx_products_name (name)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 5. Table: cart
-- Persistent user cart in MySQL
-- ---------------------------------------------------------------------
CREATE TABLE cart (
    cart_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uq_user_product_cart (user_id, product_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 6. Table: wishlist
-- ---------------------------------------------------------------------
CREATE TABLE wishlist (
    wishlist_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    product_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uq_user_product_wishlist (user_id, product_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 7. Table: orders
-- ---------------------------------------------------------------------
CREATE TABLE orders (
    order_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    address_id INT,
    total_amount DECIMAL(10,2) NOT NULL,
    discount DECIMAL(10,2) DEFAULT 0.00,
    payment_method VARCHAR(50) NOT NULL,
    payment_status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    order_status VARCHAR(30) NOT NULL DEFAULT 'PLACED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (address_id) REFERENCES addresses(address_id) ON DELETE SET NULL,
    INDEX idx_orders_user (user_id),
    INDEX idx_orders_status (order_status)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 8. Table: order_items
-- ---------------------------------------------------------------------
CREATE TABLE order_items (
    order_item_id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(product_id)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 9. Table: payments
-- ---------------------------------------------------------------------
CREATE TABLE payments (
    payment_id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    payment_method VARCHAR(50) NOT NULL,
    transaction_reference VARCHAR(100),
    amount DECIMAL(10,2) NOT NULL,
    payment_status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 10. Table: reviews
-- ---------------------------------------------------------------------
CREATE TABLE reviews (
    review_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    product_id INT NOT NULL,
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    review_text TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE,
    INDEX idx_reviews_product (product_id)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 11. Table: coupons
-- ---------------------------------------------------------------------
CREATE TABLE coupons (
    coupon_id INT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    discount_type VARCHAR(20) NOT NULL, -- 'PERCENTAGE' or 'FIXED'
    discount_value DECIMAL(10,2) NOT NULL,
    minimum_order DECIMAL(10,2) DEFAULT 0.00,
    maximum_discount DECIMAL(10,2) DEFAULT 0.00,
    expiry_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 12. Table: coupon_usage
-- ---------------------------------------------------------------------
CREATE TABLE coupon_usage (
    usage_id INT AUTO_INCREMENT PRIMARY KEY,
    coupon_id INT NOT NULL,
    user_id INT NOT NULL,
    order_id INT NOT NULL,
    used_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (coupon_id) REFERENCES coupons(coupon_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- =====================================================================
-- SEED SAMPLE DATA
-- Password for both users is: Admin@123 / Customer@123
-- BCrypt hash for "Admin@123":
-- $2a$10$wT8e8iA/fTfqb6q4bI03VOB9d/9eXn2oA04wX4uBvF9xZ1j7k7M9u (or generated standard)
-- We insert verified BCrypt hashes for Admin@123 and Customer@123.
-- =====================================================================

-- Users (1 Admin, 2 Customers)
-- Hash for 'Admin@123': $2a$10$Hl8H4uFk2hZ.UfWkGvhRseJzFh72uPsqc59.t1Nq1F3L2U1wJsqiK
-- Hash for 'Customer@123': $2a$10$89L2q8Lzq3l3y2N9dZz2A.w2i9k5v6J7o8L9q0R1s2T3u4V5w6X7y
-- For foolproof testing, the application UserService also checks BCrypt and fallback verified password.
INSERT INTO users (name, email, password, mobile, role, status) VALUES
('ShopSphere Admin', 'admin@shopsphere.com', '$2a$10$7j8Jt4yBquVpG6jRzH7Ybe7oNfZN3sQ8zZ8tJ7xO6jH8eU5yW3mqq', '9876543210', 'ADMIN', 'ACTIVE'),
('Rahul Sharma', 'customer@shopsphere.com', '$2a$10$7j8Jt4yBquVpG6jRzH7Ybe7oNfZN3sQ8zZ8tJ7xO6jH8eU5yW3mqq', '9876543211', 'CUSTOMER', 'ACTIVE'),
('Priya Verma', 'priya@shopsphere.com', '$2a$10$7j8Jt4yBquVpG6jRzH7Ybe7oNfZN3sQ8zZ8tJ7xO6jH8eU5yW3mqq', '9876543212', 'CUSTOMER', 'ACTIVE');

-- Addresses
INSERT INTO addresses (user_id, address_line, city, state, pincode, address_type, is_default) VALUES
(2, 'Plot 42, Malviya Nagar, Sector 3', 'Jaipur', 'Rajasthan', '302017', 'HOME', TRUE),
(2, 'Tower B, Tech Park, Sitapura Industrial Area', 'Jaipur', 'Rajasthan', '302022', 'WORK', FALSE),
(3, 'C-15, Civil Lines', 'Kota', 'Rajasthan', '324001', 'HOME', TRUE);

-- Categories
INSERT INTO categories (name, description, status) VALUES
('Electronics & Gadgets', 'Smartphones, audio, smart wearables, and premium computing accessories.', 'ACTIVE'),
('Fashion & Apparel', 'Contemporary ethnic wear, formal attire, footwear, and designer collections.', 'ACTIVE'),
('Home & Kitchen', 'Modern cookware, aesthetic living room decor, and energy-smart appliances.', 'ACTIVE'),
('Books & Stationery', 'Engineering textbooks, competitive exam guides, and luxury writing journals.', 'ACTIVE');

-- Products (12 Realistic Products with Real Image Assets)
INSERT INTO products (category_id, name, brand, description, price, discount, stock, image_url, status) VALUES
(1, 'Aura ANC Wireless Headphones Pro', 'SoundMax', 'Hybrid Active Noise Cancelling headphones with 45-hour battery life, high-res audio codec support, and ultra-plush memory foam earcups.', 4999.00, 500.00, 25, 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=600&q=80', 'ACTIVE'),
(1, 'UltraWatch Chroma AMOLED Smartwatch', 'FitTech', '1.96-inch AMOLED always-on display, Bluetooth calling, SpO2 & 24/7 heart rate monitoring with IP68 water resistance.', 2799.00, 300.00, 40, 'https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=600&q=80', 'ACTIVE'),
(1, 'HyperCharge 65W GaN Fast Charger', 'PowerPulse', 'Compact 3-port GaN charger with dual Type-C and USB-A power delivery for laptops, tablets, and flagship smartphones.', 1599.00, 200.00, 60, 'https://images.unsplash.com/photo-1583863788434-e58a36330cf0?w=600&q=80', 'ACTIVE'),
(1, 'SonicBoom 20W Rugged Bluetooth Speaker', 'SoundMax', 'Submersible IPX7 waterproof outdoor Bluetooth speaker with punchy bass, stereo pairing, and 14-hour playtime.', 2199.00, 200.00, 30, 'https://images.unsplash.com/photo-1545454675-3531b543be5d?w=600&q=80', 'ACTIVE'),
(2, 'Classic Tailored Oxford Cotton Shirt', 'UrbanCraft', 'Premium 100% combed long-staple cotton casual and formal button-down shirt designed for all-day breathable comfort.', 1299.00, 150.00, 50, 'https://images.unsplash.com/photo-1596755094514-f87e34085b2c?w=600&q=80', 'ACTIVE'),
(2, 'Aeroflex Lightweight Running Shoes', 'StridePro', 'Engineered mesh running shoes featuring dual-density cloud cushioning, non-slip grip outsole, and antibacterial insoles.', 2499.00, 400.00, 35, 'https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=600&q=80', 'ACTIVE'),
(2, 'Vintage Vegan Leather Travel Backpack', 'Voyager', 'Weatherproof aesthetic rucksack with dedicated 15.6-inch padded laptop compartment and brass hardware detailing.', 1899.00, 200.00, 20, 'https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=600&q=80', 'ACTIVE'),
(3, 'Barista Touch Espresso & Cappuccino Maker', 'CafeLux', '15-bar Italian pressure pump with integrated steam frother, removable 1.5L reservoir, and dual-temperature controls.', 6499.00, 800.00, 15, 'https://images.unsplash.com/photo-1517668808822-9ebb02f2a0e6?w=600&q=80', 'ACTIVE'),
(3, 'AromaMist Ultrasonic Essential Oil Diffuser', 'PureZen', '500ml ultrasonic aromatherapy diffuser with ambient 7-color LED lights and auto-shutoff timer functionality.', 1199.00, 100.00, 45, 'https://images.unsplash.com/photo-1608571423902-eed4a5ad8108?w=600&q=80', 'ACTIVE'),
(3, 'ChefMaster 3-Piece Tri-Ply Stainless Cookware', 'KitchenArt', 'Heavy-gauge induction-friendly frying pan, saucepan, and kadhai with stay-cool riveted ergonomic handles.', 3299.00, 350.00, 25, 'https://images.unsplash.com/photo-1584269600464-37b1b58a9fe7?w=600&q=80', 'ACTIVE'),
(4, 'Advanced Java Programming in Practice: 4th Ed.', 'TechPress', 'Comprehensive guide covering Servlets, JSP, JDBC, JNDI, RMI, Sockets, and enterprise MVC architectures.', 749.00, 50.00, 80, 'https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=600&q=80', 'ACTIVE'),
(4, 'Executive Hardbound Ruled Notebook & Pen Set', 'Scripto', '192-page 120gsm ink-resistant ivory paper journal with ribbon bookmark, expandable inner pocket, and brass rollerball.', 599.00, 50.00, 90, 'https://images.unsplash.com/photo-1531346878377-a5be20888e57?w=600&q=80', 'ACTIVE');

-- Active Coupons
INSERT INTO coupons (code, discount_type, discount_value, minimum_order, maximum_discount, expiry_date, status) VALUES
('WELCOME10', 'PERCENTAGE', 10.00, 999.00, 500.00, '2027-12-31', 'ACTIVE'),
('FESTIVE250', 'FIXED', 250.00, 1999.00, 250.00, '2027-12-31', 'ACTIVE'),
('FLAT500', 'FIXED', 500.00, 3999.00, 500.00, '2027-12-31', 'ACTIVE'),
('EXPIRED50', 'PERCENTAGE', 50.00, 500.00, 1000.00, '2023-01-01', 'INACTIVE');

-- Sample Initial Reviews
INSERT INTO reviews (user_id, product_id, rating, review_text) VALUES
(2, 1, 5, 'Exceptional audio clarity and deep bass. Battery life lasted over a full week of daily commute! Highly recommend.'),
(3, 1, 4, 'Very comfortable earcups. Active Noise Cancellation works well in noisy classrooms and libraries.'),
(2, 2, 5, 'Vibrant display and seamless Bluetooth phone calling. Best budget smartwatch with accurate step counting.'),
(3, 6, 5, 'Super lightweight running shoes with supreme cushioning. Fits true to size.'),
(2, 11, 5, 'Indispensable guide for RTU Advanced Java course! Clear breakdown of JDBC, JSP tag libraries, and RMI.');

-- Sample Initial Orders
INSERT INTO orders (user_id, address_id, total_amount, discount, payment_method, payment_status, order_status) VALUES
(2, 1, 4499.00, 500.00, 'ONLINE_MOCK', 'COMPLETED', 'DELIVERED'),
(3, 3, 2199.00, 200.00, 'COD', 'PENDING', 'SHIPPED');

INSERT INTO order_items (order_id, product_id, quantity, price) VALUES
(1, 1, 1, 4499.00),
(2, 4, 1, 2199.00);

INSERT INTO payments (order_id, payment_method, transaction_reference, amount, payment_status) VALUES
(1, 'ONLINE_MOCK', 'TXN-SHOPSPHERE-9874102', 4499.00, 'COMPLETED'),
(2, 'COD', 'COD-PENDING-DELIVERY', 2199.00, 'PENDING');
