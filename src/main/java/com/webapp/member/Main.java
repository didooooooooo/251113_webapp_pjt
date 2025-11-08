package com.webapp.member;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    private static final int PORT = 8080;

    public static void main(String[] args) {
        System.out.println("=== 서버 시작 ===");
        System.out.println("포트: " + PORT);
        System.out.println("URL: http://localhost:" + PORT);
        
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("서버가 실행 중입니다. 접속을 기다립니다...\n");
            
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> handleRequest(clientSocket)).start();
            }
            
        } catch (IOException e) {
            System.err.println("서버 시작 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void handleRequest(Socket socket) {
        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
             OutputStream out = socket.getOutputStream()) {

            // HTTP 요청 첫 줄 읽기
            String requestLine = in.readLine();
            if (requestLine == null) return;
            
            System.out.println("요청: " + requestLine);

            // 요청 헤더 읽기 (본문 전까지)
            String line;
            while ((line = in.readLine()) != null && !line.isEmpty()) {
                // 헤더 읽기 (필요시 처리)
            }

            // HTTP 요청 파싱
            String[] parts = requestLine.split(" ");
            String method = parts[0];
            String path = parts[1];

            // 루트 경로 처리
            if (path.equals("/")) {
                path = "/index.html";
            }

            // HTML 파일 읽기 및 응답
            String htmlContent = loadHtmlFile(path);
            
            if (htmlContent != null) {
                sendResponse(out, 200, "text/html", htmlContent);
            } else {
                String notFoundHtml = "<html><body><h1>404 Not Found</h1><p>요청한 페이지를 찾을 수 없습니다.</p></body></html>";
                sendResponse(out, 404, "text/html", notFoundHtml);
            }

        } catch (IOException e) {
            System.err.println("요청 처리 실패: " + e.getMessage());
        }
    }

    private static String loadHtmlFile(String path) {
        try {
            // resources/static 폴더에서 파일 읽기
            InputStream inputStream = Main.class.getResourceAsStream("/static" + path);
            
            if (inputStream == null) {
                System.out.println("파일을 찾을 수 없음: " + path);
                return null;
            }

            BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            StringBuilder content = new StringBuilder();
            String line;
            
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            
            reader.close();
            return content.toString();
            
        } catch (IOException e) {
            System.err.println("파일 읽기 실패: " + e.getMessage());
            return null;
        }
    }

    private static void sendResponse(OutputStream out, int statusCode, 
                                     String contentType, String body) throws IOException {
        byte[] bodyBytes = body.getBytes(StandardCharsets.UTF_8);
        
        String statusMessage = (statusCode == 200) ? "OK" : "Not Found";
        
        String response = "HTTP/1.1 " + statusCode + " " + statusMessage + "\r\n" +
                         "Content-Type: " + contentType + "; charset=UTF-8\r\n" +
                         "Content-Length: " + bodyBytes.length + "\r\n" +
                         "Connection: close\r\n" +
                         "\r\n";
        
        out.write(response.getBytes(StandardCharsets.UTF_8));
        out.write(bodyBytes);
        out.flush();
    }
}