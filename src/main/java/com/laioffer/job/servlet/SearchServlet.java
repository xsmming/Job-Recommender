package com.laioffer.job.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.laioffer.job.db.MySQLConnection;
import com.laioffer.job.entity.Item;
import com.laioffer.job.entity.ResultResponse;
import com.laioffer.job.external.GitHubClient;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Set;

@WebServlet(name = "SearchServelet", urlPatterns = {"/search"})
public class SearchServlet extends JupiterAuthenticatedHttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // auth
        if (sessionInvalid(request,response)) {
            return;
        }

        ObjectMapper mapper = new ObjectMapper();

        String userId = request.getParameter("user_id");
        double lat = Double.parseDouble(request.getParameter("lat"));
        double lon = Double.parseDouble(request.getParameter("lon"));
        MySQLConnection connection = new MySQLConnection();
        Set<String> favoritedItemIds = connection.getFavoriteItemIds(userId);
        connection.close();

        GitHubClient client = new GitHubClient();
        List<Item> items = client.search(lat, lon, null);

        for (Item item : items) {
            item.setFavorite(favoritedItemIds.contains(item.getId()));  // 小细节，省着用户加了，但是job里没有
        }

        response.setContentType("application/json");

        response.getWriter().print(mapper.writeValueAsString(items));


//
//        double lat = Double.parseDouble(request.getParameter("lat"));
//        double lon = Double.parseDouble(request.getParameter("lon"));
//
//        GitHubClient client = new GitHubClient();
//        String itemsString = client.search(lat, lon, null);
//        response.setContentType("application/json");
//        response.getWriter().print(itemsString);
    }
}
