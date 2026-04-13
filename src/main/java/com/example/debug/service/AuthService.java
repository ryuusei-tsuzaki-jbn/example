package com.example.debug.service;

import com.example.debug.model.User;
import com.example.debug.repository.UserRepository;

public class AuthService {

    private final UserRepository userRepository = new UserRepository();

    public User login(String username, String password, String ageText) {
        User user = userRepository.findByUsername(username);

        // 意図的なミス: userがnullでもそのまま参照
        if (user.getPassword().equals(password)) {
            int inputAge = Integer.parseInt(ageText);
            if (inputAge == user.getAge()) {
                return user;
            }
        }

        return null;
    }
}
