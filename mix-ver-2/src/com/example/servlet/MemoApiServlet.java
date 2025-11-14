package com.example.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.io.BufferedReader;
import com.google.gson.Gson; 

@WebServlet("/api/memo")
public class MemoApiServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final Gson gson = new Gson();

    private static class ApiResponse {
        boolean success;
        String message;
        String content; 

        ApiResponse(boolean success, String message, String content) {
            this.success = success;
            this.message = message;
            this.content = content;
        }
    }
    private static class MemoSaveRequest {
        String content;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(gson.toJson(new ApiResponse(false, "로그인이 필요합니다.", null)));
            return;
        }
        String username = (String) session.getAttribute("username");

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT content FROM memos WHERE username = ?")) {
            
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String content = rs.getString("content");
                    response.getWriter().write(gson.toJson(new ApiResponse(true, "조회 성공", content)));
                } else {
                    response.getWriter().write(gson.toJson(new ApiResponse(true, "새 메모", "")));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson(new ApiResponse(false, "DB 조회 오류: " + e.getMessage(), null)));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(gson.toJson(new ApiResponse(false, "로그인이 필요합니다.", null)));
            return;
        }
        String username = (String) session.getAttribute("username");

        BufferedReader reader = request.getReader();
        MemoSaveRequest reqData = gson.fromJson(reader, MemoSaveRequest.class);
        String newContent = reqData.content;

        String sql = "INSERT INTO memos (username, content) VALUES (?, ?) ON DUPLICATE KEY UPDATE content = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, username);
            ps.setString(2, newContent);
            ps.setString(3, newContent); 
            
            ps.executeUpdate();
            
            response.getWriter().write(gson.toJson(new ApiResponse(true, "저장되었습니다.", null)));

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson(new ApiResponse(false, "DB 저장 오류: " + e.getMessage(), null)));
        }
    }
}