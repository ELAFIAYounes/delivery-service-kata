-- Insert test orders for customer123
INSERT INTO orders (id, customer_id, order_date, status) VALUES
(1, 'customer123', DATEADD('DAY', -3, CURRENT_TIMESTAMP()), 'CONFIRMED'),
(2, 'customer123', DATEADD('DAY', -7, CURRENT_TIMESTAMP()), 'DELIVERED'),
(3, 'customer123', DATEADD('DAY', -1, CURRENT_TIMESTAMP()), 'PENDING');

-- Insert order items with gaming products
INSERT INTO order_items (id, order_id, product_id, product_name, quantity, price) VALUES
(1, 1, 'PROD-001', 'Gaming Laptop Pro', 1, 1499.99),
(2, 1, 'PROD-002', 'Gaming Mouse RGB', 1, 79.99),
(3, 2, 'PROD-003', '4K Gaming Monitor', 1, 499.99),
(4, 3, 'PROD-004', 'Mechanical Gaming Keyboard', 1, 159.99);

-- Insert a pending refund request with evidence
INSERT INTO refund_requests (order_item_id, request_date, description, evidence_image_url, status) VALUES
(1, DATEADD('DAY', -1, CURRENT_TIMESTAMP()), 'Product arrived with dead pixels', 'https://evidence.example.com/dead-pixels.jpg', 'PENDING');
