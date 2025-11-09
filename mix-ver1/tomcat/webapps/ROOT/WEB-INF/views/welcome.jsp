<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String username = (String) session.getAttribute("username");
    if (username == null) {
        // 세션이 없으면 로그인 페이지로 리다이렉트 (경로 수정)
        response.sendRedirect(request.getContextPath() + "/index.html");
        return;
    }
%>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8" />
  <title>환영합니다</title>
  <link rel="stylesheet" href="<%=request.getContextPath()%>/css/styles.css" />
</head>
<body>
  <div class="container" style="margin-top: 50px;">
    <div class="auth-card">
        <div class="logo">
             <img src="img/robot.png" alt="Robot Logo" class="robot-logo">
        </div>
        <h2 style="text-align: center; font-family: 'Jua', sans-serif; margin-bottom: 20px;">
            <%= username %>님, 환영합니다!
        </h2>
        <p style="text-align: center; margin-bottom: 30px;">로그인에 성공하셨습니다.</p>
        
        <a href="logout" class="btn-submit" style="text-decoration: none; text-align: center; line-height: 46px;">
            로그아웃
        </a>
    </div>
  </div>
</body>
</html>