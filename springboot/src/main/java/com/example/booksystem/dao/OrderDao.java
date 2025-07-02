package com.example.booksystem.dao;

import com.example.booksystem.model.Order;
import com.example.booksystem.model.OrderDetail;
import com.example.booksystem.model.OrderDetailWithBook;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class OrderDao extends JdbcDaoSupport {

    public OrderDao(DataSource dataSource) {
        setDataSource(dataSource);
    }

    public int saveOrder(Order order) {
        String sql = "INSERT INTO orders (order_no, user_id, total_amount, status) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        getJdbcTemplate().update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, order.getOrderNo());
            ps.setInt(2, order.getUserId());
            ps.setBigDecimal(3, order.getTotalAmount());
            ps.setString(4, order.getStatus());
            return ps;
        }, keyHolder);
        
        return keyHolder.getKey().intValue();
    }

    public int saveOrderDetail(OrderDetail detail) {
        String sql = "INSERT INTO order_details (order_id, book_id, quantity, price) VALUES (?, ?, ?, ?)";
        return getJdbcTemplate().update(sql, detail.getOrderId(), detail.getBookId(), detail.getQuantity(), detail.getPrice());
    }

    public Order findById(Integer id) {
        String sql = "SELECT * FROM orders WHERE id = ?";
        List<Order> orders = getJdbcTemplate().query(sql, new BeanPropertyRowMapper<>(Order.class), id);
        return orders.isEmpty() ? null : orders.get(0);
    }

    public List<Order> findByUserId(Integer userId) {
        String sql = "SELECT * FROM orders WHERE user_id = ? ORDER BY id DESC";
        return getJdbcTemplate().query(sql, new BeanPropertyRowMapper<>(Order.class), userId);
    }

    public List<Order> findByStatus(String status) {
        String sql = "SELECT * FROM orders WHERE status = ? ORDER BY id DESC";
        return getJdbcTemplate().query(sql, new BeanPropertyRowMapper<>(Order.class), status);
    }

    public List<Order> findAll() {
        String sql = "SELECT * FROM orders ORDER BY id DESC";
        return getJdbcTemplate().query(sql, new BeanPropertyRowMapper<>(Order.class));
    }

    public List<OrderDetail> findDetailsByOrderId(Integer orderId) {
        String sql = "SELECT * FROM order_details WHERE order_id = ? ORDER BY id";
        return getJdbcTemplate().query(sql, new BeanPropertyRowMapper<>(OrderDetail.class), orderId);
    }

    public int updateOrderStatus(Integer orderId, String status) {
        String sql = "UPDATE orders SET status = ? WHERE id = ?";
        return getJdbcTemplate().update(sql, status, orderId);
    }

    public boolean existsByOrderNo(String orderNo) {
        String sql = "SELECT COUNT(*) FROM orders WHERE order_no = ?";
        Integer count = getJdbcTemplate().queryForObject(sql, Integer.class, orderNo);
        return count != null && count > 0;
    }

    public List<OrderDetailWithBook> findDetailsWithBookByOrderId(Integer orderId) {
        String sql = "SELECT od.id, od.order_id, od.book_id, od.quantity, od.price, " +
                     "b.title as book_title, b.author as book_author, b.cover_url, b.description " +
                     "FROM order_details od " +
                     "LEFT JOIN books b ON od.book_id = b.id " +
                     "WHERE od.order_id = ? ORDER BY od.id";
        return getJdbcTemplate().query(sql, new BeanPropertyRowMapper<>(OrderDetailWithBook.class), orderId);
    }
}