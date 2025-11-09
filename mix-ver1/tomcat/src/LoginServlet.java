package com.example.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;

@WebServlet(urlPatterns = {"/login", "/welcome"})
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // --- ⛔️ [수정 지점] ---
    // DB 연결 정보를 실제 RDS 정보로 수정하세요.
    private String dbUrl = "jdbc:mysql://vec-prd-vpc-db-pri-2a.czywucycklo5.ap-northeast-2.rds.amazonaws.com:3306/VEC_PRD_DB?useSSL=false&serverTimezone=UTC";
    private String dbUser = "admin";
    private String dbPassword = "powermvp";
    private String dbDriver = "com.mysql.cj.jdbc.Driver";
    // --- ⛔️ [수정 끝] ---

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json"); // JSON 응답
        response.setCharacterEncoding("UTF-8");

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            Class.forName(dbDriver);
            conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
            
            ps = conn.prepareStatement("SELECT password FROM users WHERE username = ?");
            ps.setString(1, username);
            rs = ps.executeQuery();

            if (rs.next()) {
                String dbPassword = rs.getString("password");
                // (보안 경고: 실제 운영에서는 해시된 비밀번호를 비교해야 합니다.)
                if (password.equals(dbPassword)) {
                    // 로그인 성공
                    HttpSession session = request.getSession();
                    session.setAttribute("username", username);
                    // 성공 JSON 응답
                    response.getWriter().write("{\"success\": true, \"redirect\": \"welcome\"}");
                } else {
                    // 비밀번호 불일치
                    response.getWriter().write("{\"success\": false, \"message\": \"비밀번호가 일치하지 않습니다.\"}");
                }
            } else {
                // 사용자 없음
                response.getWriter().write("{\"success\": false, \"message\": \"존재하지 않는 아이디입니다.\"}");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"success\": false, \"message\": \"DB 조회 중 오류가 발생했습니다.\"}");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"success\": false, \"message\": \"DB 드라이버를 찾을 수 없습니다.\"}");
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) {}
            try { if (ps != null) ps.close(); } catch (SQLException e) {}
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // /welcome 경로 GET 요청 처리
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("username") != null) {
            request.getRequestDispatcher("/WEB-INF/views/welcome.jsp").forward(request, response);
        } else {
            response.sendRedirect("index.html"); // 세션 없으면 로그인 페이지로
        }
    }
}