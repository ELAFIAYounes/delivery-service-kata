-- Insert test delivery modes
INSERT INTO time_slots (start_time, end_time, delivery_mode, is_available) 
VALUES (DATEADD('HOUR', 1, CURRENT_TIMESTAMP()), DATEADD('HOUR', 2, CURRENT_TIMESTAMP()), 'DRIVE', true);

INSERT INTO time_slots (start_time, end_time, delivery_mode, is_available)
VALUES (DATEADD('HOUR', 2, CURRENT_TIMESTAMP()), DATEADD('HOUR', 3, CURRENT_TIMESTAMP()), 'DELIVERY', true);

INSERT INTO time_slots (start_time, end_time, delivery_mode, is_available)
VALUES (DATEADD('HOUR', 3, CURRENT_TIMESTAMP()), DATEADD('HOUR', 4, CURRENT_TIMESTAMP()), 'DELIVERY_TODAY', false);
