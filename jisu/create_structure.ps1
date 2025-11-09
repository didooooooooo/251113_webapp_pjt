# --- 설정 ---
# 1. 프로젝트를 생성할 기본 경로입니다.
#    요청하신 'C:\JISU\code\AWS_cloud_school\login-app' 경로로 설정되어 있습니다.
$basePath = "C:\JISU\code\AWS_cloud_school\login-app"

# 2. 생성할 최상위 프로젝트 폴더 이름입니다.
$projectRoot = "mix-ver1"
# --- ---

# 3. 전체 프로젝트 절대 경로를 조합합니다.
$projectPath = Join-Path -Path $basePath -ChildPath $projectRoot

# 4. 생성할 *파일* 목록 (경로 포함)
#    New-Item의 -Force 옵션은 파일 경로에 포함된 모든 상위 폴더들을
#    (존재하지 않는 경우) 자동으로 함께 생성해 줍니다.
$filesToCreate = @(
    "nginx\Dockerfile",
    "nginx\default.conf",
    "tomcat\Dockerfile",
    "tomcat\src\LoginServlet.java",
    "tomcat\src\LogoutServlet.java",
    "tomcat\src\SignupServlet.java",
    "tomcat\lib\servlet-api.jar",
    "tomcat\webapps\ROOT\index.html",
    "tomcat\webapps\ROOT\find-account.html",
    "tomcat\webapps\ROOT\css\styles.css",
    "tomcat\webapps\ROOT\js\script.js",
    "tomcat\webapps\ROOT\js\find-script.js",
    "tomcat\webapps\ROOT\img\robot.png",
    "tomcat\webapps\ROOT\img\welcome.png",
    "tomcat\webapps\ROOT\WEB-INF\lib\mysql-connector-j-8.3.0.jar",
    "tomcat\webapps\ROOT\WEB-INF\views\signup.jsp",
    "tomcat\webapps\ROOT\WEB-INF\views\welcome.jsp",
    "tomcat\webapps\ROOT\WEB-INF\web.xml"
)

# 5. 비어 있는 폴더 목록 (파일이 없는 폴더)
#    트리 구조에서 'classes' 폴더는 비어 있으므로 별도로 생성합니다.
$emptyFoldersToCreate = @(
    "tomcat\webapps\ROOT\WEB-INF\classes"
)

# --- 스크립트 실행 ---
Write-Host "프로젝트 폴더 생성을 시작합니다..." -ForegroundColor Green
Write-Host "대상 경로: $projectPath" -ForegroundColor Yellow
Write-Host "---"

# 6. 파일 및 상위 폴더 생성
foreach ($file in $filesToCreate) {
    # $projectPath 와 상대 파일 경로를 조합하여 전체 파일 경로 생성
    $filePath = Join-Path -Path $projectPath -ChildPath $file
    try {
        # -Force 옵션으로 상위 폴더가 없으면 자동 생성하며, 파일이 이미 있어도 덮어씁니다.
        New-Item -Path $filePath -ItemType File -Force -ErrorAction Stop | Out-Null
        Write-Host "  [파일 생성] $file"
    } catch {
        Write-Host "  [오류] $file 생성 실패: $_" -ForegroundColor Red
    }
}

# 7. 빈 폴더 생성
foreach ($folder in $emptyFoldersToCreate) {
    $folderPath = Join-Path -Path $projectPath -ChildPath $folder
    try {
        New-Item -Path $folderPath -ItemType Directory -Force -ErrorAction Stop | Out-Null
        Write-Host "  [폴더 생성] $folder"
    } catch {
        Write-Host "  [오류] $folder 생성 실패: $_" -ForegroundColor Red
    }
}

Write-Host "---"
Write-Host "모든 폴더와 파일 생성이 완료되었습니다." -ForegroundColor Green
Write-Host "이제 '# (사용자 제공)' 또는 '# (수동 복사 필요)' 파일을 해당 위치에 채워주세요."
Write-Host ""
Write-Host "스크립트 실행이 완료되었습니다."