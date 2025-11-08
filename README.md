## 환경설정
- chocolatey 설치
```
1. powershell을 관리자 권한으로 실행해서 아래 명령어 실행
$ Set-ExecutionPolicy Bypass -Scope Process -Force; [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))

2. 설치 확인

```
$ choco -v
```

- java 설치
```
$ choco install openjdk17 -y
```

- gradle 설치
```
$ choco install gradle -y
```

## 경로
 - src/main/java/webapp/member # JAVA
  - src/main/resources  # HTML
  - src/main/resources # CSS

## 빌드방법
1. 아래 파일이 없으면 1회 실행
```
gradlew
gradlew.bat
gradle/wrapper/gradle-wrapper.jar
gradle/wrapper/gradle-wrapper.properties
```
2. 실행 명령어 (설치한 그래들 버전으로 실행할것)
```
$ gradle wrapper --gradle-version 9.1
```
3. 빌드
```
$ ./gradlew build
```
4. 실행
```
$ ./gradlew run
```
- http://localhost:8080 확인