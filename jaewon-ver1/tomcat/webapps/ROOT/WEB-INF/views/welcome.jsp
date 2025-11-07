<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String username = (String) session.getAttribute("username");
    if (username == null) {
        response.sendRedirect("../index.html");
        return;
    }
%>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8" />
  <title>환영합니다</title>
  <link rel="stylesheet" href="<%=request.getContextPath()%>/css/login.css" />
</head>
<body>
  <div class="welcome-container" style="text-align:center; margin-top: 40px;">
    <h1><%= username %>님, 환영합니다!</h1>
    <p>로그인에 성공하셨습니다.</p>
    <form action="<%=request.getContextPath()%>/logout" method="post">
      <button type="submit" style="padding:10px 20px; font-size:16px;">로그아웃</button>
    </form>
  </div>
</body>
</html>

