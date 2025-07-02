package com.example.ds.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author shixiaojun@itany
 */
@Service
public class DeepSeekService {
    @Value("${deepseek.api.url}")
    private String apiUrl;

    @Value("${deepseek.api.key}")
    private String apiKey;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String askQuestion(String question) throws Exception {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(apiUrl);

            // 设置请求头
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("Authorization", "Bearer " + apiKey);

            // 构建请求体
            Map<String, Object> requestBody = Map.of(
                    "model", "deepseek-chat",
                    "messages", List.of(Map.of(
                            "role", "user",
                            "content", question
                    )),
                    "temperature", 0.7
            );

            httpPost.setEntity(new StringEntity(objectMapper.writeValueAsString(requestBody)));

            // 执行请求
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    String responseString = EntityUtils.toString(entity);
                    Map<String, Object> responseMap = objectMapper.readValue(responseString, Map.class);

                    // 解析响应
                    List<Map<String, Object>> choices = (List<Map<String, Object>>) responseMap.get("choices");
                    if (choices != null && !choices.isEmpty()) {
                        Map<String, Object> firstChoice = choices.get(0);
                        Map<String, String> message = (Map<String, String>) firstChoice.get("message");
                        return message.get("content");
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }
        return "抱歉，无法获取回答。";
    }
}
