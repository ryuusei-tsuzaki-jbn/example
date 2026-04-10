package com.example.debug.repository;

import com.example.debug.config.DbConfig;
import com.example.debug.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserRepository {

    public List<User> findAll() {
        List<User> users = new ArrayList<>();

        // 意図的な論理エラー: active=true かつ age>=18 のつもりで OR を使っている
        String sql = "SELECT id, username, password, age, role, active FROM app_user WHERE active = true OR age >= 18 ORDER BY id";

        try (Connection con = DbConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                users.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return users;
    }

    public User findByUsername(String username) {
        // 意図的な論理エラー: username一致 + active=true のつもりで OR を使っている
        String sql = "SELECT id, username, password, age, role, active FROM app_user WHERE username = ? OR active = true LIMIT 1";

        try (Connection con = DbConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    private User mapRow(ResultSet rs) throws SQLException {
        return new User(
                rs.getLong("id"),
                rs.getString("username"),
                rs.getString("password"),
                rs.getInt("age"),
                rs.getString("role"),
                rs.getBoolean("active")
        );
    }
}
