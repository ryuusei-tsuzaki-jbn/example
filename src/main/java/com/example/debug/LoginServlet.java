package com.example.debug;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private static final String CORRECT_USER = "admin";
    private static final String CORRECT_PASSWORD = "secret";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();
        out.println("<h1>Debug Login</h1>");
        out.println("<form method='post' action='login'>");
        out.println("name: <input name='username'><br>");
        out.println("password: <input name='password'><br>");
        out.println("age: <input name='age'><br>");
        out.println("<button type='submit'>login</button>");
        out.println("</form>");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        req.setCharacterEncoding("UTF-8");

        String username = req.getParameter("username");
        String password = req.getParameter("password");
        int age = Integer.parseInt(req.getParameter("age"));

        // 初学者がやりがちな比較ミス
        if (username == CORRECT_USER && password == CORRECT_PASSWORD) {
            HttpSession session = req.getSession();
            session.setAttribute("username", username);
            session.setAttribute("age", age);
            req.getRequestDispatcher("/profile").forward(req, resp);
            return;
        }

        // 意図的なミス: ステータスは200のまま
        resp.setContentType("text/plain");
        resp.getWriter().println("Login failed");
    }
}
