package com.laioffer.job.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.laioffer.job.db.MySQLConnection;
import com.laioffer.job.entity.HistoryRequestBody;
import com.laioffer.job.entity.Item;
import com.laioffer.job.entity.ResultResponse;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Set;

@WebServlet(name = "HistoryServlet", urlPatterns = "/history")
public class HistoryServlet extends JupiterAuthenticatedHttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (sessionInvalid(request,response)) {
            return;
        }

        ObjectMapper mapper = new ObjectMapper();
        response.setContentType("application/json");
        HistoryRequestBody body = mapper.readValue(request.getReader(), HistoryRequestBody.class);

        MySQLConnection connection = new MySQLConnection();
        connection.setFavoriteItems(body.userId, body.favorite);
        connection.close();

        ResultResponse resultResponse = new ResultResponse("SUCCESS");
        mapper.writeValue(response.getWriter(), resultResponse);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (sessionInvalid(request,response)) {
            return;
        }

        response.setContentType("application/json");
        ObjectMapper mapper = new ObjectMapper();
        // 读请求
        String userId = request.getParameter("user_id");

        MySQLConnection connection = new MySQLConnection();
        Set<Item> favoriteItems = connection.getFavoriteItems(userId);
        connection.close(); // don't forget to close ...
        // 写响应
        mapper.writeValue(response.getWriter(), favoriteItems);
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (sessionInvalid(request,response)) {
            return;
        }

        response.setContentType("application/json");
        ObjectMapper mapper = new ObjectMapper();
        HistoryRequestBody body = mapper.readValue(request.getReader(), HistoryRequestBody.class);

        MySQLConnection connection = new MySQLConnection();
        connection.unsetFavoriteItems(body.userId, body.favorite.getId()); // 和上面略有不同
        connection.close();

        ResultResponse resultResponse = new ResultResponse("SUCCESS");
        mapper.writeValue(response.getWriter(), resultResponse);
    }
}
