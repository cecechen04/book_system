package com.example.booksystem.controller;

import com.example.booksystem.model.ApiResponse;
import com.example.booksystem.model.ChatRequest;
import com.example.booksystem.model.ChatResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/chat")
@CrossOrigin(origins = "*")
public class ChatController {

    @Autowired
    private RestTemplate restTemplate;

    private static final String DEEPSEEK_SERVICE_URL = "http://localhost:8888/api/ask";

    @PostMapping("/ask")
    public ApiResponse<ChatResponse> askQuestion(@RequestBody ChatRequest request) {
        try {
            // 验证请求参数
            if (request.getQuestion() == null || request.getQuestion().trim().isEmpty()) {
                return ApiResponse.error("问题不能为空");
            }

            // 准备请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // 准备请求体，匹配deepseek服务的期望格式
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("question", request.getQuestion());

            HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);

            // 调用deepseek服务
            ResponseEntity<ChatResponse> response = restTemplate.exchange(
                DEEPSEEK_SERVICE_URL,
                HttpMethod.POST,
                entity,
                ChatResponse.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return ApiResponse.success("获取回答成功", response.getBody());
            } else {
                return ApiResponse.error("AI服务暂时不可用");
            }

        } catch (Exception e) {
            // 记录错误日志（实际项目中应使用日志框架）
            System.err.println("调用AI服务时发生错误: " + e.getMessage());
            e.printStackTrace();
            
            // 返回友好的错误信息
            return ApiResponse.error("AI服务暂时不可用，请稍后重试");
        }
    }
}