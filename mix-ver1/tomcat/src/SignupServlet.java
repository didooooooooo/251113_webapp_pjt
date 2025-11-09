package com.example.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;

@WebServlet("/signup")
public class SignupServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // --- ⛔️ [수정 지점] ---
    // DB 연결 정보를 실제 RDS 정보로 수정하세요.
    private String dbUrl = "jdbc:mysql://vec-prd-vpc-db-pri-2a.czywucycklo5.ap-northeast-2.rds.amazonaws.com:3306/VEC_PRD_DB?useSSL=false&serverTimezone=UTC";
    private String dbUser = "admin";
    private String dbPassword = "powermvp";
    private String dbDriver = "com.mysql.cj.jdbc.Driver";
    // --- ⛔️ [수정 끝] ---
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // changha-ver2의 index.html에서 회원가입 탭을 누르므로,
        // 별도의 signup.jsp 페이지로 포워딩할 필요가 없습니다.
        // GET 요청은 로그인 페이지로 리다이렉트합니다.
        response.sendRedirect("index.html");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // changha-ver2 폼 필드에 맞게 파라미터 수신
        String email = request.getParameter("email");
        String name = request.getParameter("name");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String birthdate = request.getParameter("birthdate");

        // 간단한 유효성 검사
        if (username == null || username.isEmpty() || password == null || password.isEmpty() || email == null || email.isEmpty()) {
            response.getWriter().write("{\"success\": false, \"message\": \"필수 입력값이 누락되었습니다.\"}");
            return;
        }

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            Class.forName(dbDriver);
            conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
            conn.setAutoCommit(false); // 트랜잭션 시작

            // 1. 중복 사용자 확인
            try (PreparedStatement checkStmt = conn.prepareStatement("SELECT COUNT(*) FROM users WHERE username = ?")) {
                checkStmt.setString(1, username);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        response.getWriter().write("{\"success\": false, \"message\": \"이미 존재하는 아이디입니다.\"}");
                        conn.rollback();
                        return;
                    }
                }
            }

            // 2. 사용자 정보 저장 (changha-ver2 폼에 맞춘 SQL)
            // (주의: users 테이블에 email, name, birthdate 컬럼이 있어야 합니다.)
            String sql = "INSERT INTO users (username, password, email, name, birthdate) VALUES (?, ?, ?, ?, ?)";
            ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password); // (보안 경고: 실제는 해시 필요)
            ps.setString(3, email);
            ps.setString(4, name);
            ps.setString(5, birthdate);
            
            int result = ps.executeUpdate();

            if (result > 0) {
                conn.commit();
                response.getWriter().write("{\"success\": true, \"message\": \"회원가입이 완료되었습니다.\"}");
            } else {
                conn.rollback();
                response.getWriter().write("{\"success\": false, \"message\": \"회원가입에 실패했습니다.\"}");
            }

        } catch (SQLException e) {
            try { if (conn != null) conn.rollback(); } catch (SQLException se) { se.printStackTrace(); }
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"success\": false, \"message\": \"DB 작업 중 오류가 발생했습니다.\"}");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"success\": false, \"message\": \"DB 드라이버를 찾을 수 없습니다.\"}");
        } finally {
            try { if (ps != null) ps.close(); } catch (SQLException e) {}
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
    }
}