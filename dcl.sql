-- =======================================================
-- RAPA AWS 12기 수업 도우미 (mix-ver2) DB 초기화 스크립트
-- =======================================================

-- 1. 사용할 데이터베이스를 선택합니다. (⛔️ 'yourdb'를 실제 DB 이름으로 변경하세요!)
USE VEC_PRD_DB;


-- 2. (기능 0) 사용자 테이블
-- changha-ver2 폼에 맞게 name, birthdate 컬럼이 포함됩니다.
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(60) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    
    -- changha-ver2 폼에서 추가로 받는 항목들
    name VARCHAR(100) NULL,
    birthdate VARCHAR(8) NULL,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


-- 3. (기능 1) 메모 테이블
-- 한 사용자가 하나의 메모만 가진다고 가정 (단순화)
CREATE TABLE IF NOT EXISTS memos (
  id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50) NOT NULL UNIQUE,
  content TEXT NULL,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE
);


-- 4. (기능 2) 투두리스트 테이블
CREATE TABLE IF NOT EXISTS todos (
  id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50) NOT NULL,
  task VARCHAR(255) NOT NULL,
  is_completed BOOLEAN DEFAULT FALSE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE
);


-- 5. (기능 3) 링크 모음 테이블
CREATE TABLE IF NOT EXISTS links (
  id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50) NULL, -- NULL이면 모든 사용자에게 보이는 '기본 링크'
  link_name VARCHAR(100) NOT NULL,
  url VARCHAR(1000) NOT NULL
);


-- 6. (기능 4-A) 강사님 암기 항목 테이블 (관리자가 미리 입력)
CREATE TABLE IF NOT EXISTS memorize_items (
  id INT AUTO_INCREMENT PRIMARY KEY,
  item_text VARCHAR(1000) NOT NULL,
  sort_order INT DEFAULT 0
);


-- 7. (기능 4-B) 사용자별 암기 상태 테이블
CREATE TABLE IF NOT EXISTS user_memorize_status (
  id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50) NOT NULL,
  item_id INT NOT NULL,
  is_memorized BOOLEAN DEFAULT FALSE,
  FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE,
  FOREIGN KEY (item_id) REFERENCES memorize_items(id) ON DELETE CASCADE,
  UNIQUE KEY(username, item_id) -- 한 사용자가 한 항목에 대해 하나의 상태만 갖도록
);


-- 8. (기능 5) GPT 질문/답변 기록 테이블
CREATE TABLE IF NOT EXISTS gpt_history (
  id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50) NOT NULL,
  question TEXT NOT NULL,
  answer TEXT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE
);


-- =======================================================
-- 3단계: (선택 사항) 기본 데이터 삽입
-- =======================================================

-- 1. 테스트용 관리자 계정 (ID: admin / PW: admin)
INSERT INTO users (username, password, email, name, birthdate) VALUES
('admin', 'admin', 'admin@admin.com', '관리자', '19900101')
ON DUPLICATE KEY UPDATE username=username;

-- 2. 모든 사용자에게 보일 기본 링크
INSERT INTO links (username, link_name, url) VALUES
(NULL, '쿠버 바이블', 'https://kubernetes.io/docs/home/'),
(NULL, '쿠버 노션 자료', 'https://powermvp.notion.site/RAPA12-kubernetes-2a6de383d1a180e28368fddb3fd50954'),
(NULL, 'AWS Console', 'https://console.aws.amazon.com/')

ON DUPLICATE KEY UPDATE url=VALUES(url);

-- 3. 기본 암기 항목 리스트
INSERT INTO memorize_items (item_text, sort_order) VALUES
('/etc/kubernetes/manifests (static yaml 경로)', 1),
('/var/lib/docker/overlay2 (도커 이미지 경로)', 2),
('/var/lib/docker/containers (컨테이너 경로)', 3),
('/usr/share/nginx/html  (Nginx html 경로)', 4),
('/var/www/html/index.html   (apache 경로)', 5)
ON DUPLICATE KEY UPDATE item_text=VALUES(item_text);


FLUSH PRIVILEGES;

SELECT 'mix-ver2 DB setup complete.' AS status;