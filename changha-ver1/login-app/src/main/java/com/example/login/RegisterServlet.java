package com.example.login;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UserDAO userDAO;
    
    public void init() {
        userDAO = new UserDAO();
    }
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        // 회원가입 페이지로 포워드 (이미 index.html에서 처리하므로 여기서는 생략)
        response.sendRedirect("index.html");
    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        // 요청 파라미터 인코딩 설정
        request.setCharacterEncoding("UTF-8");
        
        // 회원가입 폼에서 전송된 파라미터 가져오기
        String email = request.getParameter("email");
        String username = request.getParameter("reg-username");
        String password = request.getParameter("reg-password");
        String confirmPassword = request.getParameter("confirm-password");
        
        try {
            // 필수 필드 검증
            if (email == null || username == null || password == null || 
                email.trim().isEmpty() || username.trim().isEmpty() || password.trim().isEmpty()) {
                request.setAttribute("errorMessage", "모든 필드를 입력해주세요.");
                request.getRequestDispatcher("index.html?tab=register&error=1").forward(request, response);
                return;
            }
            
            // 비밀번호 일치 확인
            if (!password.equals(confirmPassword)) {
                request.setAttribute("errorMessage", "비밀번호가 일치하지 않습니다.");
                request.getRequestDispatcher("index.html?tab=register&error=2").forward(request, response);
                return;
            }
            
            // 사용자명 중복 검증
            if (userDAO.isUsernameExists(username)) {
                request.setAttribute("errorMessage", "이미 사용 중인 아이디입니다.");
                request.getRequestDispatcher("index.html?tab=register&error=3").forward(request, response);
                return;
            }
            
            // 모든 검증 통과 - 새 사용자 생성
            User newUser = new User(username, password, email);
            userDAO.insertUser(newUser);
            
            // 회원가입 성공 - 로그인 페이지로 리다이렉트
            response.sendRedirect("index.html?success=1");
        } catch (Exception e) {
            // 예외 발생 시 에러 페이지로 리다이렉트
            e.printStackTrace();
            response.sendRedirect("error.jsp");
        }
    }
}
