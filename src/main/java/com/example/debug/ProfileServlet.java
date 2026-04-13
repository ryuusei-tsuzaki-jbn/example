package com.example.debug;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {

    private final UserRepository userRepository = new UserRepository();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);

        // 意図的なミス: sessionがnullでもそのまま使う
        String username = (String) session.getAttribute("username");

        Integer visits = (Integer) session.getAttribute("visits");
        visits++;
        session.setAttribute("visits", visits);

        List<String> users = userRepository.findAll();

        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();
        out.println("<h1>Profile</h1>");
        out.println("<p>Hello, " + username.toUpperCase() + "</p>");
        out.println("<p>Visit count: " + visits + "</p>");
        out.println("<p>All users: " + users.get(2) + "</p>");
    }
}
