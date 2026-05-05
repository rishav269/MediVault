-- ─────────────────────────────────────────────────────────────
-- MediVault — MySQL Setup Script
-- Run once before starting the application.
-- ─────────────────────────────────────────────────────────────

-- 1. Create the database
CREATE DATABASE IF NOT EXISTS medivault;
USE medivault;

-- 2. Create the patients table
CREATE TABLE IF NOT EXISTS patients (
    id    INT AUTO_INCREMENT PRIMARY KEY,
    name  VARCHAR(100) NOT NULL,
    age   INT          NOT NULL,
    phone VARCHAR(15)  NOT NULL
);

-- 3. (Optional) Insert sample rows to verify View Patients works
INSERT INTO patients (name, age, phone) VALUES
    ('Alice Johnson', 34, '9876543210'),
    ('Bob Smith',     52, '9123456780'),
    ('Carol White',   28, '9001122334');

-- Verify
SELECT * FROM patients;

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('ADMIN', 'STAFF') NOT NULL
);

-- Note: In a real app, use BCrypt to generate this hash.
-- This is a placeholder for '123' hashed.
INSERT INTO users (username, password_hash, role) VALUES
('admin', '$2a$10$7vM.8p8P.Y.8k7zG8y...', 'ADMIN'),
('staff1', '$2a$10$7vM.8p8P.Y.8k7zG8y...', 'STAFF');
