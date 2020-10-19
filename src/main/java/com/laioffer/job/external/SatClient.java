package com.laioffer.job.external;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;


public class SatClient {

    public String request(HttpServletRequest request) {
        // step 1. received from XVisual, and to generate the url for calling N2YO
        String lat = request.getParameter("lat");
        String lon = request.getParameter("lon");
        String alt = request.getParameter("alt");
        String radius = request.getParameter("radius");
        String satCategory = request.getParameter("satCategory");
        final String baseUrl = "https://api.n2yo.com/rest/v1/satellite";
        final String apiKey = "TSPJRE-KXUWXJ-WM67Y2-4K0T";
        final String URL_TEMPLATE =
                baseUrl + "/above/%s/%s/%s/%s/%s/&apiKey=%s";
        String url = String.format(URL_TEMPLATE,lat,lon,alt,radius,satCategory,apiKey);

        //  step 2. call N2YO and ready to receive response from it by JSON format
        ResponseHandler<String> responseHandler = responseFromN2YO -> { //lambda
            if (responseFromN2YO.getStatusLine().getStatusCode() != 200) {  // failed
                return "";
            }
            HttpEntity entity = responseFromN2YO.getEntity();
            if (entity == null) {
                return "";
            }
            return null;    // 需要解决：如何将从A服务器拿回来的HttpResponse
                            // 内容不变用HttpServletResponse的形式返回给B
        };
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            return httpclient.execute(new HttpGet(url),responseHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }



        // step 3. organize the response data and get back to XVisual
        return "TRY FIELD AFTER N2YO";
    }

}
