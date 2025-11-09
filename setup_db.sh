#!/bin/bash

# 1. 시간대가 Asia/Seoul이 아니면 변경
CURRENT_TZ=$(timedatectl show --property=Timezone --value)
if [ "$CURRENT_TZ" != "Asia/Seoul" ]; then
  echo "Changing timezone to Asia/Seoul"
  sudo timedatectl set-timezone Asia/Seoul
else
  echo "Timezone already set to Asia/Seoul"
fi

# 2. MySQL bind-address 확인 및 변경
BIND_ADDRESS=$(grep -E '^bind-address' /etc/mysql/mysql.conf.d/mysqld.cnf | awk '{print $3}')
if [ "$BIND_ADDRESS" != "0.0.0.0" ]; then
  echo "Changing bind-address to 0.0.0.0"
  sudo sed -i 's/^bind-address\s*=.*/bind-address = 0.0.0.0/' /etc/mysql/mysql.conf.d/mysqld.cnf
  sudo systemctl restart mysql
else
  echo "bind-address already set to 0.0.0.0"
fi

# 3. MySQL DB 및 테이블 세팅
sudo mysql -u root -p <<EOF
CREATE DATABASE IF NOT EXISTS yourdb DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER IF NOT EXISTS 'dbuser'@'%' IDENTIFIED BY 'dbpassword';
GRANT ALL PRIVILEGES ON yourdb.* TO 'dbuser'@'%';
FLUSH PRIVILEGES;

USE yourdb;
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(60) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO users (username, password, email) VALUES
('admin', 'admin', 'admin@admin')
ON DUPLICATE KEY UPDATE username=username;
EOF

