package com.example.servlet;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.*;

@WebServlet("/signup")
public class SignupServlet extends HttpServlet {

    private DataSource dataSource;

    @Override
    public void init() throws ServletException {
        try {
            InitialContext ctx = new InitialContext();
            dataSource = (DataSource) ctx.lookup("java:comp/env/jdbc/mgw");
        } catch (NamingException e) {
            throw new ServletException("DB 연결 실패", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 회원가입 페이지 JSP로 포워딩
        request.getRequestDispatcher("/WEB-INF/views/signup.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String passwordConfirm = request.getParameter("passwordConfirm");
        String email = request.getParameter("email");

        // 간단한 유효성 검사
        if (username == null || username.isEmpty() || password == null || password.isEmpty()
            || email == null || email.isEmpty() || !password.equals(passwordConfirm)) {

            request.setAttribute("errorMessage", "입력값을 확인하세요. 비밀번호와 확인이 일치해야 합니다.");
            request.getRequestDispatcher("/WEB-INF/views/signup.jsp").forward(request, response);
            return;
        }

        try (Connection conn = dataSource.getConnection()) {
            // 중복 사용자 확인
            try (PreparedStatement checkStmt = conn.prepareStatement("SELECT COUNT(*) FROM users WHERE username = ?")) {
                checkStmt.setString(1, username);
                ResultSet rs = checkStmt.executeQuery();
                rs.next();
                if (rs.getInt(1) > 0) {
                    request.setAttribute("errorMessage", "이미 존재하는 아이디입니다.");
                    request.getRequestDispatcher("/WEB-INF/views/signup.jsp").forward(request, response);
                    return;
                }
            }

            // 사용자 정보 저장
            try (PreparedStatement insertStmt = conn.prepareStatement("INSERT INTO users (username, password, email) VALUES (?, ?, ?)")) {
                insertStmt.setString(1, username);
                insertStmt.setString(2, password); // 실제 서비스에서는 해시 적용 필수
                insertStmt.setString(3, email);
                insertStmt.executeUpdate();
            }

            // 가입 성공 시 로그인 페이지로 이동
            response.sendRedirect(request.getContextPath() + "/index.html");

        } catch (SQLException e) {
            throw new ServletException("DB 작업 중 오류 발생", e);
        }
    }
}

