package com.example.booksystem.service;

import com.example.booksystem.dao.BookDao;
import com.example.booksystem.model.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    @Autowired
    private BookDao bookDao;

    public List<Book> getAllBooks() {
        return bookDao.findAll();
    }

    public Book getBookById(Integer id) {
        return bookDao.findById(id);
    }

    public List<Book> getBooksByCategoryId(Integer categoryId) {
        return bookDao.findByCategoryId(categoryId);
    }

    public List<Book> searchBooks(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllBooks();
        }
        return bookDao.searchByKeyword(keyword.trim());
    }

    public boolean saveBook(Book book) {
        return bookDao.save(book) > 0;
    }

    public boolean updateBook(Book book) {
        return bookDao.update(book) > 0;
    }

    public boolean deleteBook(Integer id) {
        return bookDao.delete(id) > 0;
    }

    public boolean hasEnoughStock(Integer bookId, Integer quantity) {
        return bookDao.hasEnoughStock(bookId, quantity);
    }

    public boolean reduceStock(Integer bookId, Integer quantity) {
        return bookDao.updateStock(bookId, quantity) > 0;
    }
}