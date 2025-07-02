package com.example.booksystem.dao;

import com.example.booksystem.model.ShoppingCart;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class ShoppingCartDao extends JdbcDaoSupport {

    public ShoppingCartDao(DataSource dataSource) {
        setDataSource(dataSource);
    }

    public List<ShoppingCart> findByUserId(Integer userId) {
        String sql = "SELECT * FROM shopping_cart WHERE user_id = ? ORDER BY id";
        return getJdbcTemplate().query(sql, new BeanPropertyRowMapper<>(ShoppingCart.class), userId);
    }

    public ShoppingCart findByUserIdAndBookId(Integer userId, Integer bookId) {
        String sql = "SELECT * FROM shopping_cart WHERE user_id = ? AND book_id = ?";
        List<ShoppingCart> items = getJdbcTemplate().query(sql, new BeanPropertyRowMapper<>(ShoppingCart.class), userId, bookId);
        return items.isEmpty() ? null : items.get(0);
    }

    public int save(ShoppingCart cart) {
        String sql = "INSERT INTO shopping_cart (user_id, book_id, quantity) VALUES (?, ?, ?)";
        return getJdbcTemplate().update(sql, cart.getUserId(), cart.getBookId(), cart.getQuantity());
    }

    public int updateQuantity(Integer userId, Integer bookId, Integer quantity) {
        String sql = "UPDATE shopping_cart SET quantity = ? WHERE user_id = ? AND book_id = ?";
        return getJdbcTemplate().update(sql, quantity, userId, bookId);
    }

    public int delete(Integer userId, Integer bookId) {
        String sql = "DELETE FROM shopping_cart WHERE user_id = ? AND book_id = ?";
        return getJdbcTemplate().update(sql, userId, bookId);
    }

    public int deleteByUserId(Integer userId) {
        String sql = "DELETE FROM shopping_cart WHERE user_id = ?";
        return getJdbcTemplate().update(sql, userId);
    }
}