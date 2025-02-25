DROP TABLE IF EXISTS time_slots;

CREATE TABLE time_slots (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    start_time TEXT NOT NULL,
    end_time TEXT NOT NULL,
    delivery_mode TEXT NOT NULL,
    is_available INTEGER NOT NULL DEFAULT 1
);
