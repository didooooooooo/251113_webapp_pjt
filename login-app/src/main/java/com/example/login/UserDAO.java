package com.example.login;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    // MariaDB 연결 설정으로 변경
    private String jdbcURL = "jdbc:mysql://mariadb:3306/loginapp";
    private String jdbcUsername = "root";
    private String jdbcPassword = "password";
    
    private static final String INSERT_USERS_SQL = "INSERT INTO users (username, password, email) VALUES (?, ?, ?);";
    private static final String SELECT_USER_BY_ID = "SELECT * FROM users WHERE id = ?;";
    private static final String SELECT_USER_BY_USERNAME = "SELECT * FROM users WHERE username = ?;";
    private static final String SELECT_ALL_USERS = "SELECT * FROM users;";
    private static final String DELETE_USER_SQL = "DELETE FROM users WHERE id = ?;";
    private static final String UPDATE_USER_SQL = "UPDATE users SET username = ?, password = ?, email = ? WHERE id = ?;";
    private static final String CHECK_USERNAME_EXISTS = "SELECT COUNT(*) FROM users WHERE username = ?;";
    
    protected Connection getConnection() {
        Connection connection = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return connection;
    }
    
    // 새 사용자 추가
    public void insertUser(User user) throws SQLException {
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_USERS_SQL)) {
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.setString(3, user.getEmail());
            preparedStatement.executeUpdate();
        }
    }
    
    // ID로 사용자 조회
    public User getUserById(int id) {
        User user = null;
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_USER_BY_ID)) {
            preparedStatement.setInt(1, id);
            ResultSet rs = preparedStatement.executeQuery();
            
            while (rs.next()) {
                String username = rs.getString("username");
                String password = rs.getString("password");
                String email = rs.getString("email");
                user = new User(id, username, password, email);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }
    
    // 사용자명으로 사용자 조회
    public User getUserByUsername(String username) {
        User user = null;
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_USER_BY_USERNAME)) {
            preparedStatement.setString(1, username);
            ResultSet rs = preparedStatement.executeQuery();
            
            while (rs.next()) {
                int id = rs.getInt("id");
                String password = rs.getString("password");
                String email = rs.getString("email");
                user = new User(id, username, password, email);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }
    
    // 모든 사용자 목록 조회
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_USERS)) {
            ResultSet rs = preparedStatement.executeQuery();
            
            while (rs.next()) {
                int id = rs.getInt("id");
                String username = rs.getString("username");
                String password = rs.getString("password");
                String email = rs.getString("email");
                users.add(new User(id, username, password, email));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }
    
    // 사용자 정보 업데이트
    public boolean updateUser(User user) throws SQLException {
        boolean rowUpdated;
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_USER_SQL)) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getEmail());
            statement.setInt(4, user.getId());
            
            rowUpdated = statement.executeUpdate() > 0;
        }
        return rowUpdated;
    }
    
    // 사용자 삭제
    public boolean deleteUser(int id) throws SQLException {
        boolean rowDeleted;
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_USER_SQL)) {
            statement.setInt(1, id);
            rowDeleted = statement.executeUpdate() > 0;
        }
        return rowDeleted;
    }
    
    // 사용자명 중복 체크
    public boolean isUsernameExists(String username) {
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(CHECK_USERNAME_EXISTS)) {
            preparedStatement.setString(1, username);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // 로그인 검증
    public boolean validateUser(String username, String password) {
        User user = getUserByUsername(username);
        return user != null && user.getPassword().equals(password);
    }
}
