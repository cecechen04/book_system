package com.example.ds;

import com.example.ds.service.DeepSeekKeyValidator;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DeepseekApplication {
    @Value("${deepseek.api.key}")
    private String apiKey;
    public static void main(String[] args) {
        SpringApplication.run(DeepseekApplication.class, args);
    }
    @PostConstruct
    public void validateApiKeyOnStartup() {
        DeepSeekKeyValidator validator = new DeepSeekKeyValidator();
        boolean isValid = validator.validateApiKey(apiKey);

        if (!isValid) {
            System.err.println("⚠️ DeepSeek API Key验证失败！请检查配置");
            // 可以选择抛出异常终止应用启动
            throw new RuntimeException("Invalid DeepSeek API Key");
        } else {
            System.out.println("✅ DeepSeek API Key验证成功");
        }
    }
}
