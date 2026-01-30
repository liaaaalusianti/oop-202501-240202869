-- Buat database agripos
CREATE DATABASE IF NOT EXISTS agripos;
USE agripos;

-- Buat tabel products
CREATE TABLE IF NOT EXISTS products (
    code VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    price DOUBLE NOT NULL,
    stock INT NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
