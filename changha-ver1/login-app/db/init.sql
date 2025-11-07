CREATE DATABASE IF NOT EXISTS loginapp;
USE loginapp;

CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 테스트용 사용자 추가 (비밀번호: password)
INSERT INTO users (username, password, email) 
VALUES ('admin', 'password', 'admin@example.com'),
       ('user', 'password', 'user@example.com');
