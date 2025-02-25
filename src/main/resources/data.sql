-- Insert time slots for DRIVE mode
INSERT INTO time_slots (start_time, end_time, delivery_mode, is_available) VALUES
('2025-02-24 10:00:00', '2025-02-24 11:00:00', 'DRIVE', 1),
('2025-02-24 11:00:00', '2025-02-24 12:00:00', 'DRIVE', 1),
('2025-02-24 14:00:00', '2025-02-24 15:00:00', 'DRIVE', 1);

-- Insert time slots for DELIVERY mode
INSERT INTO time_slots (start_time, end_time, delivery_mode, is_available) VALUES
('2025-02-24 09:00:00', '2025-02-24 10:00:00', 'DELIVERY', 1),
('2025-02-24 13:00:00', '2025-02-24 14:00:00', 'DELIVERY', 1);

-- Insert time slots for DELIVERY_TODAY mode
INSERT INTO time_slots (start_time, end_time, delivery_mode, is_available) VALUES
('2025-02-24 15:00:00', '2025-02-24 16:00:00', 'DELIVERY_TODAY', 1),
('2025-02-24 16:00:00', '2025-02-24 17:00:00', 'DELIVERY_TODAY', 1);

-- Insert time slots for DELIVERY_ASAP mode
INSERT INTO time_slots (start_time, end_time, delivery_mode, is_available) VALUES
('2025-02-24 09:30:00', '2025-02-24 10:30:00', 'DELIVERY_ASAP', 1),
('2025-02-24 10:30:00', '2025-02-24 11:30:00', 'DELIVERY_ASAP', 1);
