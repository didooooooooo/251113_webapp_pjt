package com.example.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import com.google.gson.Gson;

@WebServlet("/api/gpt")
public class GptApiServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // API 키를 환경 변수에서 읽어옵니다.
    private static final String OPENAI_API_KEY = System.getenv("OPENAI_API_KEY");

    private final Gson gson = new Gson();

    // --- JSON 헬퍼 클래스들 ---
    private static class ApiResponse {
        boolean success; String message; Object data;
        ApiResponse(boolean success, String message, Object data) {
            this.success = success; this.message = message; this.data = data;
        }
    }
    private static class GptHistoryItem {
        int id; String question; String answer; String created_at;
    }
    private static class GptPromptRequest {
        String prompt;
    }
    private static class GptResponse { GptChoice[] choices; }
    private static class GptChoice { GptMessage message; }
    private static class GptMessage { String role; String content; }

    // OpenAI API 요청용 클래스 (JSON 주입 방지)
    private static class GptRequest {
        String model;
        List<GptRequestMessage> messages;
        GptRequest(String model, String prompt) {
            this.model = model;
            this.messages = new ArrayList<>();
            this.messages.add(new GptRequestMessage("user", prompt));
        }
    }
    private static class GptRequestMessage {
        String role; String content;
        GptRequestMessage(String role, String content) {
            this.role = role; this.content = content;
        }
    }
    // --- 헬퍼 클래스 끝 ---

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

        List<GptHistoryItem> historyList = new ArrayList<>();
        String sql = "SELECT id, question, answer, created_at FROM gpt_history WHERE username = ? ORDER BY created_at ASC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    GptHistoryItem item = new GptHistoryItem();
                    item.id = rs.getInt("id");
                    item.question = rs.getString("question");
                    item.answer = rs.getString("answer");
                    item.created_at = rs.getTimestamp("created_at").toString();
                    historyList.add(item);
                }
            }
            response.getWriter().write(gson.toJson(new ApiResponse(true, "조회 성공", historyList)));

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
        GptPromptRequest reqData = gson.fromJson(reader, GptPromptRequest.class);
        String prompt = reqData.prompt;

        if (prompt == null || prompt.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson(new ApiResponse(false, "질문 내용이 비어있습니다.", null)));
            return;
        }

        try {
            String answer = callOpenAiApi(prompt);
            String sql = "INSERT INTO gpt_history (username, question, answer) VALUES (?, ?, ?)";
            GptHistoryItem newHistoryItem = new GptHistoryItem();

            try (Connection conn = DatabaseConfig.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                
                ps.setString(1, username);
                ps.setString(2, prompt);
                ps.setString(3, answer);
                ps.executeUpdate();

                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        newHistoryItem.id = generatedKeys.getInt(1);
                        newHistoryItem.question = prompt;
                        newHistoryItem.answer = answer;
                        newHistoryItem.created_at = new Timestamp(System.currentTimeMillis()).toString();
                    } else {
                        throw new SQLException("ID 생성 실패");
                    }
                }
            } 
            
            response.getWriter().write(gson.toJson(new ApiResponse(true, "질문 성공", newHistoryItem)));

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson(new ApiResponse(false, "GPT API 호출 또는 DB 저장 중 오류: " + e.getMessage(), null)));
        }
    }

    private String callOpenAiApi(String prompt) throws IOException {
        if (OPENAI_API_KEY == null || OPENAI_API_KEY.trim().isEmpty()) {
            throw new IOException("API 키가 설정되지 않았습니다. (OPENAI_API_KEY 환경 변수 필요)");
        }

        String gptApiUrl = "https://api.openai.com/v1/chat/completions";

        GptRequest gptRequest = new GptRequest("gpt-3.5-turbo", prompt);
        String jsonInputString = gson.toJson(gptRequest);

        URL url = new URL(gptApiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; utf-8");
        conn.setRequestProperty("Authorization", "Bearer " + OPENAI_API_KEY);
        conn.setRequestProperty("Accept", "application/json");
        conn.setDoOutput(true);

        try(OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);           
        }

        StringBuilder responseBody = new StringBuilder();
        try(BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                responseBody.append(responseLine.trim());
            }
        } catch (IOException e) {
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8))) {
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    responseBody.append(responseLine.trim());
                }
            }
            throw new IOException("GPT API 요청 실패. 응답: " + responseBody.toString(), e);
        }
        
        GptResponse gptResponse = gson.fromJson(responseBody.toString(), GptResponse.class);

        if (gptResponse != null && gptResponse.choices != null && gptResponse.choices.length > 0) {
            return gptResponse.choices[0].message.content;
        } else {
            throw new IOException("GPT API로부터 유효한 답변을 받지 못했습니다. 응답: " + responseBody.toString());
        }
    } // <-- 누락되었던 중괄호
}
