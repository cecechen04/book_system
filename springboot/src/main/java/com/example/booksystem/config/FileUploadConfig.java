package com.example.booksystem.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class FileUploadConfig implements WebMvcConfigurer {

    @Value("${file.upload.path:uploads/}")
    private String uploadPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置静态资源映射，让上传的文件可以通过URL访问
        // 使用绝对路径确保路径正确
        String absolutePath = System.getProperty("user.dir") + "/" + uploadPath;
        
        // 由于context-path=/api，静态资源映射也会受到影响
        // 因此/files/**实际上映射到/api/files/**
        registry.addResourceHandler("/files/**")
                .addResourceLocations("file:" + absolutePath + "/");
    }
}
