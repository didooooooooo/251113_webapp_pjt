<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>대시보드</title>
    <link rel="stylesheet" href="css/common.css">
    <link rel="stylesheet" href="css/style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">
</head>
<body>
    <div class="login-container" style="max-width:600px;">
        <div class="dashboard">
            <h2>환영합니다, <%= session.getAttribute("username") %>님!</h2>
            <p>성공적으로 로그인하셨습니다.</p>
            
            <div class="dashboard-menu">
                <div class="dashboard-item">
                    <i class="fas fa-user"></i>
                    <h3>내 정보</h3>
                    <p>개인정보 확인 및 수정</p>
                </div>
                <div class="dashboard-item">
                    <i class="fas fa-cog"></i>
                    <h3>설정</h3>
                    <p>계정 설정 관리</p>
                </div>
                <div class="dashboard-item">
                    <i class="fas fa-bell"></i>
                    <h3>알림</h3>
                    <p>알림 확인 및 설정</p>
                </div>
                <div class="dashboard-item">
                    <i class="fas fa-question-circle"></i>
                    <h3>도움말</h3>
                    <p>자주 묻는 질문</p>
                </div>
            </div>
            
            <a href="logout.jsp" class="form-button">로그아웃</a>
        </div>
    </div>
    
    <style>
        .dashboard {
            padding: 20px;
        }
        
        .dashboard h2 {
            color: #03c75a;
            margin-bottom: 20px;
        }
        
        .dashboard-menu {
            display: grid;
            grid-template-columns: repeat(2, 1fr);
            gap: 20px;
            margin: 30px 0;
        }
        
        .dashboard-item {
            background-color: #f9f9f9;
            border-radius: 8px;
            padding: 20px;
            text-align: center;
            transition: all 0.3s;
        }
        
        .dashboard-item:hover {
            transform: translateY(-5px);
            box-shadow: 0 5px 15px rgba(0,0,0,0.1);
        }
        
        .dashboard-item i {
            font-size: 30px;
            color: #03c75a;
            margin-bottom: 15px;
        }
        
        .dashboard-item h3 {
            margin-bottom: 8px;
            color: #333;
        }
        
        .dashboard-item p {
            color: #777;
            font-size: 14px;
        }
        
        @media (max-width: 480px) {
            .dashboard-menu {
                grid-template-columns: 1fr;
            }
        }
    </style>
</body>
</html>
