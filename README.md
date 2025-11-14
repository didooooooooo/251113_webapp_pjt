# 251113_webapp_pjt
이 프로젝트는 웹 애플리케이션을 구현하고 배포하는 것을 목표로 합니다.

📝 프로젝트 개요
저장소 설명: webapp 만들어서 배포하기

이 프로젝트는 Java를 기반으로 한 웹 애플리케이션으로 보이며, HTML, CSS, JavaScript를 사용한 프론트엔드와 Java(아마도 Servlet/JSP 또는 Spring)를 사용한 백엔드를 포함하고 있습니다.

dcl.sql 파일이 있는 것으로 보아, 데이터베이스를 사용하며 관련 권한 설정(DCL)을 포함하고 있습니다.

🛠️ 사용된 기술
저장소의 언어 구성을 바탕으로 추론한 기술 스택입니다.

프론트엔드:

HTML

CSS

JavaScript

백엔드:

Java

데이터베이스:

SQL (관련 파일: dcl.sql)

기타:

Shell

Batchfile (스크립트 파일)

📁 프로젝트 구조
저장소의 최상위 레벨에는 다음과 같은 주요 파일 및 디렉토리가 있습니다.

.
├── mix-ver-2/    # 웹 애플리케이션의 주요 소스 코드가 포함된 디렉토리로 추정
└── dcl.sql       # 데이터베이스 권한 관리를 위한 SQL 스크립트
🚀 시작하기
이 프로젝트를 로컬 환경에서 실행하기 위한 일반적인 단계입니다. (프로젝트의 세부 설정에 따라 달라질 수 있습니다.)

1. 전제 조건
Java Development Kit (JDK)

Apache Tomcat 또는 유사한 웹 애플리케이션 서버 (WAS)

프로젝트에서 사용하는 데이터베이스 시스템 (예: MySQL, Oracle, PostgreSQL 등)

2. 설치
저장소 복제:

Bash

git clone https://github.com/didooooooooo/251113_webapp_pjt.git
cd 251113_webapp_pjt
데이터베이스 설정:

사용 중인 데이터베이스에 접속합니다.

dcl.sql 스크립트의 내용을 참고하여 필요한 사용자 및 권한을 설정합니다.

(만약 ddl.sql이나 스키마 파일이 있다면 해당 파일로 테이블을 생성합니다.)

프로젝트 빌드:

프로젝트가 Maven 또는 Gradle을 사용하는 경우, 의존성을 설치합니다.

Bash

# Maven의 경우
mvn clean install
# Gradle의 경우
./gradlew build
빌드가 완료되면 .war 파일이 생성될 수 있습니다.

3. 실행
생성된 .war 파일을 Tomcat 등 WAS의 webapps 디렉토리에 배포합니다.

WAS를 시작합니다.

웹 브라우저에서 http://localhost:8080/{프로젝트이름} 과 같은 주소로 접속합니다.
