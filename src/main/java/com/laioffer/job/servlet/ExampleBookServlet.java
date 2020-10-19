package com.laioffer.job.servlet;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;

@WebServlet(name = "ExampleBookServlet", urlPatterns = {"/example_book"})
public class ExampleBookServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        BufferedReader reader = request.getReader();
        String jsonRawString = IOUtils.toString(reader);
        System.out.println("raw = " + jsonRawString);



        JSONObject jsonRequest = new JSONObject(IOUtils.toString(request.getReader())); //可以转换成string
        //then read all field
        String title = jsonRequest.getString("title");
        String author = jsonRequest.getString("author");
        String date = jsonRequest.getString("date");
        float price = jsonRequest.getFloat("price");
        String currency = jsonRequest.getString("currency");
        int pages = jsonRequest.getInt("pages");
        String series = jsonRequest.getString("series");
        String language = jsonRequest.getString("language");
        String isbn = jsonRequest.getString("isbn");

        //return result, and the result is also a json object, so
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("status", "ok");
        response.getWriter().print(jsonResponse);

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.getWriter().print("This is the example book servlet");
        String s1 = request.getParameter("keyword");
        String s2 = request.getParameter("category");

        System.out.println(s1+" and " +s2);

        // ... existing code, do not remove or replace
        response.setContentType("application/json");
        JSONObject json = new JSONObject();

        json.put("title", "Harry Potter and the Sorcerer's Stone");
        json.put("author", "JK Rowling");
        json.put("date", "October 1, 1998");
        json.put("price", 11.99);
        json.put("currency", "USD");
        json.put("pages", 309);
        json.put("series", "Harry Potter");
        json.put("language", "en_US");
        json.put("isbn_10", "0590353403");
        response.getWriter().print(json);

        //TODO: database server's thing (chef)
    }
}
