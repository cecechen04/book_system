package com.example.booksystem.service;

import com.example.booksystem.dao.UserDao;
import com.example.booksystem.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    @Value("${app.admin.username}")
    private String adminUsername;

    @Value("${app.admin.password}")
    private String adminPassword;

    public User login(String username, String password) {
        // 管理员硬编码登录
        if (adminUsername.equals(username) && adminPassword.equals(password)) {
            User admin = new User();
            admin.setId(0);
            admin.setUsername(adminUsername);
            admin.setRole("admin");
            return admin;
        }

        // 普通用户登录
        User user = userDao.findByUsername(username);
        if (user != null && password.equals(user.getPassword())) {
            return user;
        }
        return null;
    }

    public boolean register(String username, String password) {
        // 检查用户名是否已存在
        if (userDao.existsByUsername(username)) {
            return false;
        }

        // 创建新用户
        User user = new User(username, password, "user");
        return userDao.save(user) > 0;
    }

    public User findById(Integer id) {
        return userDao.findById(id);
    }

    public User findByUsername(String username) {
        return userDao.findByUsername(username);
    }

    public boolean existsByUsername(String username) {
        return userDao.existsByUsername(username);
    }
}