package com.example.debug.controller;

import com.example.debug.model.User;
import com.example.debug.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {

    private final UserRepository userRepository = new UserRepository();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);

        // 意図的なミス: loginUserが無い場合の考慮がない
        User loginUser = (User) session.getAttribute("loginUser");

        Integer visits = (Integer) session.getAttribute("visits");
        visits++;
        session.setAttribute("visits", visits);

        List<User> allUsers = userRepository.findAll();

        req.setAttribute("username", loginUser.getUsername().toUpperCase());
        req.setAttribute("visits", visits);
        req.setAttribute("thirdUser", allUsers.get(2));
        req.getRequestDispatcher("/WEB-INF/jsp/profile.jsp").forward(req, resp);
    }
}
