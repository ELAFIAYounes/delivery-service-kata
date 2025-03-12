-- Insert sample orders
INSERT INTO orders (customer_id, order_date, status) VALUES
('customer123', '2025-03-10 14:30:00', 'DELIVERED'),
('customer123', '2025-03-09 10:15:00', 'DELIVERED'),
('customer123', '2025-03-11 09:00:00', 'PENDING');

-- Insert sample order items
INSERT INTO order_items (order_id, product_id, product_name, quantity, price) VALUES
(1, 'PROD-001', 'Laptop XPS 13', 1, 1299.99),
(1, 'PROD-002', 'Wireless Mouse', 1, 29.99),
(2, 'PROD-003', 'External Monitor', 1, 299.99),
(3, 'PROD-004', 'Mechanical Keyboard', 1, 149.99);

-- Insert sample refund request
INSERT INTO refund_requests (order_item_id, description, evidence_image_url, request_date, status) VALUES
(1, 'Product arrived damaged', 'https://evidence-bucket.example.com/images/damage-001.jpg', '2025-03-11 10:00:00', 'PENDING');
