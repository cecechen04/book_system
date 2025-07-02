package com.example.booksystem.controller;

import com.example.booksystem.model.ApiResponse;
import com.example.booksystem.model.LoginRequest;
import com.example.booksystem.model.User;
import com.example.booksystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ApiResponse<User> login(@Valid @RequestBody LoginRequest request) {
        User user = userService.login(request.getUsername(), request.getPassword());
        if (user != null) {
            // 移除密码信息
            user.setPassword(null);
            return ApiResponse.success("登录成功", user);
        } else {
            return ApiResponse.error("用户名或密码错误");
        }
    }

    @PostMapping("/register")
    public ApiResponse<String> register(@Valid @RequestBody LoginRequest request) {
        // 检查用户名是否已存在
        if (userService.existsByUsername(request.getUsername())) {
            return ApiResponse.error("用户名已存在");
        }

        // 注册用户
        boolean success = userService.register(request.getUsername(), request.getPassword());
        if (success) {
            return ApiResponse.success("注册成功");
        } else {
            return ApiResponse.error("注册失败");
        }
    }

    @GetMapping("/check-username")
    public ApiResponse<Boolean> checkUsername(@RequestParam String username) {
        boolean exists = userService.existsByUsername(username);
        return ApiResponse.success(!exists);
    }
}