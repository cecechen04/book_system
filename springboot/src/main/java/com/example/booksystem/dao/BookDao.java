package com.example.booksystem.dao;

import com.example.booksystem.model.Book;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class BookDao extends JdbcDaoSupport {

    public BookDao(DataSource dataSource) {
        setDataSource(dataSource);
    }

    public List<Book> findAll() {
        String sql = "SELECT * FROM books ORDER BY id";
        return getJdbcTemplate().query(sql, new BeanPropertyRowMapper<>(Book.class));
    }

    public Book findById(Integer id) {
        String sql = "SELECT * FROM books WHERE id = ?";
        List<Book> books = getJdbcTemplate().query(sql, new BeanPropertyRowMapper<>(Book.class), id);
        return books.isEmpty() ? null : books.get(0);
    }

    public List<Book> findByCategoryId(Integer categoryId) {
        String sql = "SELECT * FROM books WHERE category_id = ? ORDER BY id";
        return getJdbcTemplate().query(sql, new BeanPropertyRowMapper<>(Book.class), categoryId);
    }

    public List<Book> searchByKeyword(String keyword) {
        String sql = "SELECT * FROM books WHERE title LIKE ? OR author LIKE ? ORDER BY id";
        String searchPattern = "%" + keyword + "%";
        return getJdbcTemplate().query(sql, new BeanPropertyRowMapper<>(Book.class), searchPattern, searchPattern);
    }

    public int save(Book book) {
        String sql = "INSERT INTO books (title, author, price, stock, category_id, cover_url, description) VALUES (?, ?, ?, ?, ?, ?, ?)";
        return getJdbcTemplate().update(sql, book.getTitle(), book.getAuthor(), book.getPrice(), book.getStock(), book.getCategoryId(), book.getCoverUrl(), book.getDescription());
    }

    public int update(Book book) {
        String sql = "UPDATE books SET title = ?, author = ?, price = ?, stock = ?, category_id = ?, cover_url = ?, description = ? WHERE id = ?";
        return getJdbcTemplate().update(sql, book.getTitle(), book.getAuthor(), book.getPrice(), book.getStock(), book.getCategoryId(), book.getCoverUrl(), book.getDescription(), book.getId());
    }

    public int delete(Integer id) {
        String sql = "DELETE FROM books WHERE id = ?";
        return getJdbcTemplate().update(sql, id);
    }

    public int updateStock(Integer bookId, Integer quantity) {
        String sql = "UPDATE books SET stock = stock - ? WHERE id = ? AND stock >= ?";
        return getJdbcTemplate().update(sql, quantity, bookId, quantity);
    }

    public boolean hasEnoughStock(Integer bookId, Integer quantity) {
        String sql = "SELECT stock FROM books WHERE id = ?";
        Integer stock = getJdbcTemplate().queryForObject(sql, Integer.class, bookId);
        return stock != null && stock >= quantity;
    }
}