package com.example.login;

import java.io.IOException;

import jakarta.servlet.ServletException;
// import jakarta.servlet.annotation.WebServlet; // 주석 처리
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

// @WebServlet("/login") // 주석 처리
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UserDAO userDAO;
    
    public void init() {
        userDAO = new UserDAO();
    }
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        // 로그인 페이지로 포워드 (이미 index.html에서 처리하므로 여기서는 생략)
        response.sendRedirect("index.html");
    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        // 요청 파라미터 인코딩 설정
        request.setCharacterEncoding("UTF-8");
        
        // 로그인 폼에서 전송된 파라미터 가져오기
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        
        try {
            // 로그인 정보 검증
            boolean isValidUser = userDAO.validateUser(username, password);
            
            if (isValidUser) {
                // 로그인 성공 - 세션에 사용자 정보 저장
                HttpSession session = request.getSession();
                session.setAttribute("username", username);
                
                // 로그인 성공 후 대시보드 페이지로 리다이렉트
                response.sendRedirect("dashboard.jsp");
            } else {
                // 로그인 실패 - 에러 메시지와 함께 다시 로그인 페이지로
                request.setAttribute("errorMessage", "아이디 또는 비밀번호가 올바르지 않습니다.");
                request.getRequestDispatcher("index.html?error=1").forward(request, response);
            }
        } catch (Exception e) {
            // 예외 발생 시 에러 페이지로 리다이렉트
            e.printStackTrace();
            response.sendRedirect("error.jsp");
        }
    }
}
