<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>오류</title>
    <link rel="stylesheet" href="css/common.css">
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
    <div class="login-container">
        <div class="error-container">
            <h2>오류가 발생했습니다</h2>
            <p>요청을 처리하는 중에 문제가 발생했습니다. 다시 시도해 주세요.</p>
            <p>오류 내용: <%= exception != null ? exception.getMessage() : "알 수 없는 오류" %></p>
            <a href="index.html" class="form-button">로그인 페이지로 돌아가기</a>
        </div>
    </div>
    
    <style>
        .error-container {
            padding: 30px;
            text-align: center;
        }
        
        .error-container h2 {
            color: #e74c3c;
            margin-bottom: 20px;
        }
        
        .error-container p {
            margin-bottom: 20px;
            color: #555;
        }
    </style>
</body>
</html>
