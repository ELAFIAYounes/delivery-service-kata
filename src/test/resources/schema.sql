-- Drop tables in reverse order to handle foreign key constraints
DROP TABLE IF EXISTS refund_requests;
DROP TABLE IF EXISTS order_items;
DROP TABLE IF EXISTS orders;

-- Orders table for customer order history
CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id VARCHAR(255) NOT NULL,
    order_date TIMESTAMP NOT NULL,
    status VARCHAR(50) NOT NULL
);

-- Order items table for products in each order
CREATE TABLE order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id VARCHAR(255) NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    quantity INT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id)
);

-- Refund requests table for customer refund management
CREATE TABLE refund_requests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_item_id BIGINT NOT NULL,
    description TEXT NOT NULL,
    evidence_image_url VARCHAR(1024) NOT NULL,
    request_date TIMESTAMP NOT NULL,
    status VARCHAR(50) NOT NULL,
    FOREIGN KEY (order_item_id) REFERENCES order_items(id),
    CONSTRAINT unique_refund_request UNIQUE (order_item_id)
);
