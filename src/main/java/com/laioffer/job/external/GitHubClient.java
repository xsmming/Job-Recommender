package com.laioffer.job.external;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.laioffer.job.entity.Item;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

public class GitHubClient {
    // %s 的地方就是我们可以放东西的地方，一个是经度/ 一个是维度
    private static final String URL_TEMPLATE =
            "https://jobs.github.com/positions.json?description=%s&lat=%s&long=%s";
    private static final String DEFAULT_KEYWORD = "";

    public List<Item> search(double lat, double log, String keyword) {
        // 暂时return String，下节课会return 我们的entity class
        if (keyword == null) {
            keyword = DEFAULT_KEYWORD;
        }
        // A B --> A%20B
        try {
            keyword = URLEncoder.encode(keyword,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String url = String.format(URL_TEMPLATE,keyword,lat,log);   //填充通配符
        CloseableHttpClient httpclient = HttpClients.createDefault();
        //call back method
        ResponseHandler<List<Item>> responseHandler = response -> { //lambda
            if (response.getStatusLine().getStatusCode() != 200) {  // failed
                return Collections.emptyList();
            }
            HttpEntity entity = response.getEntity();
            if (entity == null) {
                return Collections.emptyList();
            }
            ObjectMapper mapper = new ObjectMapper();
            //把String转换成entity. 和之前在search servelet用到的刚好相反，那里是entyti -> string
            // return Arrays.asList(mapper.readValue(entity.getContent(), Item[].class));
            List<Item> items = Arrays.asList(mapper.readValue(entity.getContent(), Item[].class));
            extractKeywords(items);
            return items;
        };
        try {
            return httpclient.execute(new HttpGet(url),responseHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    private void extractKeywords(List<Item> items) {
        MonkeyLearnClient monkeyLearnClient = new MonkeyLearnClient();

        List<String> descriptions = new ArrayList<>();
        for (Item item: items) {
            descriptions.add(item.getDescription());
        }
        //也可以用流操作 (pipeline)
        List<Set<String>> keywordList = new ArrayList<>();
        keywordList = monkeyLearnClient.extract(descriptions);
        for (int i = 0; i < items.size(); i++) {
            items.get(i).setKeywords(keywordList.get(i));
        }
    }
}
