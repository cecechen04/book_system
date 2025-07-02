package com.example.ds.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

/**
 * @author shixiaojun@itany
 */
@Service
public class DeepSeekKeyValidator {

    private static final String VALIDATION_URL = "https://api.deepseek.com/v1/chat/completions";
    private final ObjectMapper objectMapper = new ObjectMapper();

    public boolean validateApiKey(String apiKey) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(VALIDATION_URL);

            // 设置请求头
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("Authorization", "Bearer " + apiKey);

            // 构建最小化请求体
            String requestBody = "{\"model\":\"deepseek-chat\",\"messages\":[{\"role\":\"user\",\"content\":\"test\"}]}";
            httpPost.setEntity(new StringEntity(requestBody));

            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                int statusCode = response.getStatusLine().getStatusCode();

                // 200-299表示成功
                if (statusCode >= 200 && statusCode < 300) {
                    return true;
                }

                // 读取错误响应
                String responseBody = EntityUtils.toString(response.getEntity());
                System.err.println("API Key验证失败，响应: " + responseBody);
                return false;
            }
        } catch (Exception e) {
            System.err.println("验证过程中发生异常: " + e.getMessage());
            return false;
        }
    }
}

