package com.example.booksystem.controller;

import com.example.booksystem.model.ApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/upload")
@CrossOrigin(origins = "*")
public class FileUploadController {

    @Value("${file.upload.path:uploads/}")
    private String uploadPath;

    @Value("${server.port:8080}")
    private String serverPort;

    @PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            // 验证文件
            if (file.isEmpty()) {
                return ApiResponse.error("文件不能为空");
            }

            // 验证文件类型
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ApiResponse.error("只能上传图片文件");
            }

            // 验证文件大小（5MB）
            if (file.getSize() > 5 * 1024 * 1024) {
                return ApiResponse.error("文件大小不能超过5MB");
            }

            // 创建上传目录 - 使用绝对路径
            String absoluteUploadPath = System.getProperty("user.dir") + "/" + uploadPath;
            File uploadDir = new File(absoluteUploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String fileName = UUID.randomUUID().toString() + extension;

            // 保存文件
            Path filePath = Paths.get(absoluteUploadPath, fileName);
            Files.write(filePath, file.getBytes());

            // 返回文件URL - 使用新的静态资源映射路径
            String fileUrl = "http://localhost:" + serverPort + "/api/files/" + fileName;
            
            Map<String, String> result = new HashMap<>();
            result.put("url", fileUrl);
            result.put("filename", fileName);

            return ApiResponse.success("上传成功", result);

        } catch (IOException e) {
            return ApiResponse.error("文件上传失败: " + e.getMessage());
        }
    }
}
