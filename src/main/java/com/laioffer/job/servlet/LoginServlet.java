package com.laioffer.job.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.laioffer.job.db.MySQLConnection;
import com.laioffer.job.entity.LoginRequestBody;
import com.laioffer.job.entity.LoginResponseBody;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

    // 看一位用户有没有login，就是看这他有没有session
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        LoginRequestBody body = mapper.readValue(request.getReader(), LoginRequestBody.class);
        MySQLConnection connection = new MySQLConnection();
        LoginResponseBody loginResponseBody;

        // important! these are things in "session"
        if (connection.verifyLogin(body.userId, body.password)) { // 到数据库里去查账号密码是否匹配

            // 看源码comment，返回当前session，如无，创建一个新的session
            HttpSession session = request.getSession(); // will be mapped in cookie id
            session.setAttribute("user_id", body.userId);

            // 告诉client,你的账号/密码是正确的，request是ok的
            loginResponseBody = new LoginResponseBody("OK", body.userId, connection.getFullname(body.userId));
        } else {
            loginResponseBody = new LoginResponseBody("Login failed, user id and passcode do not exist.", null, null);
            response.setStatus(401);
        }
        connection.close();
        response.setContentType("application/json");
        mapper.writeValue(response.getWriter(), loginResponseBody);
    }

    // 这个方法实际中很少用到，可以作为check session状态的功能
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 如果没有session， 不要创建
        ObjectMapper mapper = new ObjectMapper();
        HttpSession session = request.getSession(false);    // false就代表我不要创建新的
        LoginResponseBody loginResponseBody;
        if (session != null) {
            MySQLConnection connection = new MySQLConnection();
            String userId = session.getAttribute("user_id").toString();
            loginResponseBody = new LoginResponseBody("OK", userId, connection.getFullname(userId));
            connection.close();
        } else {
            loginResponseBody = new LoginResponseBody("Invalid Session.", null, null);
            response.setStatus(403);
        }
        response.setContentType("application/json");
        mapper.writeValue(response.getWriter(), loginResponseBody);
    }
}
