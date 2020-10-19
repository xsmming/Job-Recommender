package com.laioffer.job.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.laioffer.job.entity.ExampleCoordinates;
import com.laioffer.job.entity.ExampleJob;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "ExampleJobServlet", urlPatterns = {"/example_job"})
public class ExampleJobServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");    // step1. 设置返回的数据格式为JSON

        ObjectMapper mapper = new ObjectMapper();   // 创建一个Jackson ObjectMap

        ExampleCoordinates coordinates = new ExampleCoordinates(37.485130, -122.148316);
        // creating POJO class
        ExampleJob job = new ExampleJob("Software Engineer", 123456, "Aug 1, 2020", false, coordinates);

        response.getWriter().print(mapper.writeValueAsString(job));
    }
}
