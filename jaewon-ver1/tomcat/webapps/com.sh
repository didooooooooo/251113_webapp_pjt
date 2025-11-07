#!/usr/bin/env bash
set -euo pipefail

# --- 0) 프로젝트 루트 고정 ---
APP_ROOT="$(cd "$(dirname "$0")" && pwd)"
cd "$APP_ROOT"

# --- 1) servlet-api.jar 경로 (니니 경로 우선, 없으면 CATALINA_HOME 대체) ---
SERVLET_API="$HOME/tomcatnew/tomcat/lib/servlet-api.jar"
if [ ! -f "$SERVLET_API" ]; then
  if [ -n "${CATALINA_HOME:-}" ] && [ -f "$CATALINA_HOME/lib/servlet-api.jar" ]; then
    SERVLET_API="$CATALINA_HOME/lib/servlet-api.jar"
  else
    echo "[ERROR] servlet-api.jar를 찾을 수 없습니다."
    echo "        \$HOME/tomcatnew/tomcat/lib/servlet-api.jar 또는 \$CATALINA_HOME/lib/servlet-api.jar 확인!"
    exit 1
  fi
fi

# --- 2) 디렉토리/라이브러리 준비 확인 ---
mkdir -p ROOT/WEB-INF/classes
if [ ! -d "ROOT/WEB-INF/lib" ]; then
  echo "[ERROR] WEB-INF/lib 디렉토리가 없습니다."
  exit 1
fi

# 필수 라이브러리 점검: jBCrypt, MySQL 커넥터(이름이 다를 수 있어 와일드카드 허용)
MYSQL_JAR=$(ls ROOT/WEB-INF/lib/mysql-connector-*.jar WEB-INF/lib/mysql-connector-j-*.jar 2>/dev/null || true)
if [ -z "$MYSQL_JAR" ]; then
  echo "[WARN] MySQL 커넥터 JAR를 찾지 못했습니다. DB를 쓰지 않으면 무시 가능."
fi

# --- 3) 클래스패스 구성 (servlet-api + WEB-INF/lib/* 전체) ---
CP="$SERVLET_API:/ROOT/WEB-INF/lib/*"

# --- 4) 소스 목록 생성 (src 전체 컴파일) ---
# 개별 파일 나열보다 전체를 컴파일하면 패키지 경로 누락을 방지할 수 있음
mapfile -t SRC_FILES < <(find src -type f -name "*.java" | sort)
if [ "${#SRC_FILES[@]}" -eq 0 ]; then
  echo "[ERROR] src/*.java 파일을 찾지 못했습니다."
  exit 1
fi

echo "[INFO] javac 시작..."
javac -encoding UTF-8 -cp "$CP" -d ROOT/WEB-INF/classes "${SRC_FILES[@]}"

echo "[OK] 컴파일 완료!"
echo
echo "[INFO] 산출물 확인:"
ls -la ROOT/WEB-INF/classes || true
echo
# 패키지 경로 예시 출력 (com/example/servlet 존재하면 보여줌)
if [ -d "WEB-INF/classes/com/example/servlet" ]; then
  echo "[INFO] com/example/servlet 패키지 산출물:"
  ls -la ROOT/WEB-INF/classes/com/example/servlet || true
fi

docker restart tc
