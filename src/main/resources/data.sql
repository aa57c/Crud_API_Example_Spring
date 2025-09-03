-- Note: created_at and updated_at will be automatically set by JPA auditing
-- But for initial data, we need to provide them

insert into student (id, name, passport_number, age, email, enrollment_date, graduation_year, status, created_at, updated_at, version)
values (10001, 'Ranga Karanam', 'E1234567', 28, 'ranga@example.com', '2023-09-01 09:00:00', 2026, 'ACTIVE', '2023-09-01 09:00:00', '2023-09-01 09:00:00', 0);

insert into student (id, name, passport_number, age, email, enrollment_date, graduation_year, status, created_at, updated_at, version)
values (10002, 'Ravi Kumar', 'A1234568', 25, 'ravi@example.com', '2023-09-01 10:00:00', 2025, 'ACTIVE', '2023-09-01 10:00:00', '2023-09-01 10:00:00', 0);

insert into student (id, name, passport_number, age, email, enrollment_date, graduation_year, status, created_at, updated_at, version)
values (10003, 'Sarah Johnson', 'B9876543', 22, 'sarah@example.com', '2024-01-15 14:30:00', 2025, 'SUSPENDED', '2024-01-15 14:30:00', '2025-01-15 11:20:00', 1);