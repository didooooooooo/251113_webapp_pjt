# 빌드 스테이지: Eclipse Temurin(이전 AdoptOpenJDK) JDK 이미지 사용
FROM eclipse-temurin:11-jdk-focal AS build
WORKDIR /app

# Java 소스 파일 복사
COPY src /app/src
COPY webapp/WEB-INF/lib /app/lib

# 클래스 파일 컴파일 (필요한 라이브러리 포함)
RUN mkdir -p /app/classes && \
    javac -cp "/app/lib/*" -d /app/classes $(find /app/src -name "*.java")

# 최종 스테이지: Tomcat 사용
FROM tomcat:10.0-jdk11-temurin-focal
WORKDIR /usr/local/tomcat

# 기존 Tomcat 웹앱 제거
RUN rm -rf /usr/local/tomcat/webapps/* && \
    rm -rf /usr/local/tomcat/webapps.dist

# 웹앱 디렉토리 구조 생성
RUN mkdir -p /usr/local/tomcat/webapps/ROOT/WEB-INF/classes
RUN mkdir -p /usr/local/tomcat/webapps/ROOT/WEB-INF/lib

# 웹앱 파일 복사
COPY webapp /usr/local/tomcat/webapps/ROOT

# 컴파일된 클래스 파일 복사
COPY --from=build /app/classes /usr/local/tomcat/webapps/ROOT/WEB-INF/classes

# JDBC 드라이버 및 서블릿 API JAR 파일 복사
COPY webapp/WEB-INF/lib/*.jar /usr/local/tomcat/webapps/ROOT/WEB-INF/lib/

# 로그 디렉토리 생성 및 권한 설정
RUN mkdir -p /usr/local/tomcat/logs && \
    chmod -R 755 /usr/local/tomcat/webapps/ROOT && \
    chmod -R 755 /usr/local/tomcat/logs

# 8080 포트 노출
EXPOSE 8080

# Tomcat 실행
CMD ["catalina.sh", "run"]
