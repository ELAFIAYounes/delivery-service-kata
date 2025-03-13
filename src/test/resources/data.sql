-- Test data for customer order history
INSERT INTO orders (id, customer_id, order_date, status)
VALUES 
    (1, 'customer123', DATEADD('DAY', -1, CURRENT_TIMESTAMP()), 'DELIVERED'),
    (2, 'customer123', DATEADD('DAY', -2, CURRENT_TIMESTAMP()), 'DELIVERED'),
    (3, 'customer123', DATEADD('DAY', -5, CURRENT_TIMESTAMP()), 'DELIVERED');

-- Order items for order #1 (Recent order)
INSERT INTO order_items (id, order_id, product_id, product_name, quantity, price)
VALUES 
    (101, 1, 'PROD-001', 'Gaming Laptop XPS 15', 1, 1499.99),
    (102, 1, 'PROD-002', 'Gaming Mouse', 1, 79.99);

-- Order items for order #2 (Previous order)
INSERT INTO order_items (id, order_id, product_id, product_name, quantity, price)
VALUES 
    (103, 2, 'PROD-003', 'Mechanical Keyboard', 1, 129.99),
    (104, 2, 'PROD-004', 'Gaming Headset', 1, 89.99);

-- Order items for order #3 (Older order with refund request)
INSERT INTO order_items (id, order_id, product_id, product_name, quantity, price)
VALUES 
    (105, 3, 'PROD-005', 'Gaming Monitor', 1, 299.99),
    (106, 3, 'PROD-006', 'HDMI Cable', 2, 19.99);

-- Test data for refund requests
INSERT INTO refund_requests (id, order_item_id, description, evidence_image_url, request_date, status)
VALUES 
    (1, 105, 'Dead pixels on the screen', '/evidence/monitor-dead-pixels.jpg', DATEADD('DAY', -3, CURRENT_TIMESTAMP()), 'PENDING');
