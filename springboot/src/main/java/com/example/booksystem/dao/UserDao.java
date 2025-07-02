package com.example.booksystem.dao;

import com.example.booksystem.model.User;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class UserDao extends JdbcDaoSupport {

    public UserDao(DataSource dataSource) {
        setDataSource(dataSource);
    }

    public User findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        List<User> users = getJdbcTemplate().query(sql, new BeanPropertyRowMapper<>(User.class), username);
        return users.isEmpty() ? null : users.get(0);
    }

    public User findById(Integer id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        List<User> users = getJdbcTemplate().query(sql, new BeanPropertyRowMapper<>(User.class), id);
        return users.isEmpty() ? null : users.get(0);
    }

    public int save(User user) {
        String sql = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
        return getJdbcTemplate().update(sql, user.getUsername(), user.getPassword(), user.getRole());
    }

    public boolean existsByUsername(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        Integer count = getJdbcTemplate().queryForObject(sql, Integer.class, username);
        return count != null && count > 0;
    }

    public List<User> findAll() {
        String sql = "SELECT * FROM users ORDER BY id";
        return getJdbcTemplate().query(sql, new BeanPropertyRowMapper<>(User.class));
    }
}