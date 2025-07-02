package com.example.ds.controller;

import com.example.ds.service.DeepSeekService;
import com.example.ds.util.AnswerResponse;
import com.example.ds.util.QuestionRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author shixiaojun@itany
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class QuestionController {
    private final DeepSeekService deepSeekService;
    @PostMapping("/ask")
    public AnswerResponse askQuestion(@RequestBody QuestionRequest request) {
        AnswerResponse response = new AnswerResponse();
        try {
            String answer = deepSeekService.askQuestion(request.getQuestion());
            response.setAnswer(answer);
        } catch (Exception e) {
            response.setAnswer("处理请求时发生错误: " + e.getMessage());
        }
        return response;
    }
}
