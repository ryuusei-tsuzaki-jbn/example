package com.example.debug.repository;

import com.example.debug.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserRepository {

    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        users.add(new User("alice", "alice123", 20));
        users.add(new User("bob", "bob123", 21));
        return users;
    }

    public User findByUsername(String username) {
        for (User user : findAll()) {
            // 意図的なミス: equalsではなく==で比較
            if (user.getUsername() == username) {
                return user;
            }
        }
        return null;
    }
}
