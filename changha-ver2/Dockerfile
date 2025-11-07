# 빌드 스테이지
FROM maven:3.8-openjdk-11-slim AS build
WORKDIR /app
COPY src /app/src
# WAR 파일 없이 정적 파일만 사용하므로 간단한 pom.xml 생성
RUN echo '<project xmlns="http://maven.apache.org/POM/4.0.0" \
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" \
    xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 \
    https://maven.apache.org/xsd/maven-4.0.0.xsd"> \
    <modelVersion>4.0.0</modelVersion> \
    <groupId>com.example</groupId> \
    <artifactId>login-web</artifactId> \
    <version>1.0-SNAPSHOT</version> \
    <packaging>war</packaging> \
    <properties> \
        <maven.compiler.source>11</maven.compiler.source> \
        <maven.compiler.target>11</maven.compiler.target> \
    </properties> \
    <build> \
        <finalName>login-web</finalName> \
    </build> \
</project>' > pom.xml
RUN mkdir -p /app/src/main/webapp/WEB-INF
RUN echo '<web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee" \
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" \
    xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee \
    http://xmlns.jcp.org/xml/ns/javaee/web-app_4_0.xsd" \
    version="4.0"> \
    <display-name>Login Application</display-name> \
    <welcome-file-list> \
        <welcome-file>index.html</welcome-file> \
    </welcome-file-list> \
</web-app>' > /app/src/main/webapp/WEB-INF/web.xml

# 디버깅용 - 이미지 디렉토리 확인
RUN echo "==== src 디렉토리 내용 ====" && \
    ls -la /app/src/main/webapp/img/ || echo "이미지 디렉토리가 존재하지 않습니다"

RUN mvn clean package

# 런타임 스테이지 (Tomcat Alpine)
FROM tomcat:9.0-jdk11-openjdk-slim
LABEL maintainer="Your Name <your.email@example.com>"
LABEL description="Tomcat for Login Web Application"

# 보안 강화: 불필요한 파일 제거
RUN rm -rf /usr/local/tomcat/webapps/*
RUN rm -rf /usr/local/tomcat/temp/* /usr/local/tomcat/work/*

# Tomcat 기본 웹앱 디렉토리 생성
RUN mkdir -p /usr/local/tomcat/webapps/ROOT

# 빌드 스테이지에서 생성된 정적 파일 복사
COPY --from=build /app/src/main/webapp/ /usr/local/tomcat/webapps/ROOT/

# 이미지 디렉토리 확인 (디버깅용)
RUN echo "==== 최종 webapps 디렉토리 내용 ====" && \
    ls -la /usr/local/tomcat/webapps/ROOT/ && \
    echo "==== 이미지 디렉토리 내용 ====" && \
    ls -la /usr/local/tomcat/webapps/ROOT/img/ || echo "이미지 디렉토리가 존재하지 않습니다"

# Tomcat 설정 최적화
RUN sed -i 's/connectionTimeout="20000"/connectionTimeout="10000"/g' /usr/local/tomcat/conf/server.xml
RUN sed -i 's/redirectPort="8443"/redirectPort="8443" compression="on" compressionMinSize="2048" noCompressionUserAgents="gozilla, traviata" compressableMimeType="text\/html,text\/xml,text\/plain,text\/css,text\/javascript,application\/javascript,application\/json"/g' /usr/local/tomcat/conf/server.xml

# 환경 변수 설정
ENV CATALINA_OPTS="-Xms256m -Xmx512m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=256m -Djava.security.egd=file:/dev/./urandom"

# 포트 설정
EXPOSE 8080

# 헬스체크 추가
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s CMD wget -q --spider http://localhost:8080/ || exit 1

# Tomcat 실행
CMD ["catalina.sh", "run"]
