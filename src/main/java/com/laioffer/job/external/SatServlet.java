package com.laioffer.job.external;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.laioffer.job.entity.Item;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.ResponseHandler;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@WebServlet(name = "SatServlet", urlPatterns = "/SatServlet")
public class SatServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("Hello, X-Visual from Post");
        response.setContentType("application/json");
        response.getWriter().print("received");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("Hello, X-Visual from Get");
        SatClient client = new SatClient();
        client.request(request);

        response.setContentType("application/json");
        response.getWriter().print("received");
    }
}
