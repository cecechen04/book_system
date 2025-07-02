package com.example.booksystem.controller;

import com.example.booksystem.model.ApiResponse;
import com.example.booksystem.model.Book;
import com.example.booksystem.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
@CrossOrigin(origins = "*", maxAge = 3600)
public class BookController {

    @Autowired
    private BookService bookService;

    // 获取所有图书
    @GetMapping
    public ApiResponse<List<Book>> getAllBooks() {
        List<Book> books = bookService.getAllBooks();
        return ApiResponse.success(books);
    }

    // 根据ID获取图书详情
    @GetMapping("/{id}")
    public ApiResponse<Book> getBookById(@PathVariable Integer id) {
        Book book = bookService.getBookById(id);
        if (book != null) {
            return ApiResponse.success(book);
        } else {
            return ApiResponse.error("图书不存在");
        }
    }

    // 根据分类ID获取图书
    @GetMapping("/category/{categoryId}")
    public ApiResponse<List<Book>> getBooksByCategoryId(@PathVariable Integer categoryId) {
        List<Book> books = bookService.getBooksByCategoryId(categoryId);
        return ApiResponse.success(books);
    }

    // 搜索图书
    @GetMapping("/search")
    public ApiResponse<List<Book>> searchBooks(@RequestParam(required = false) String keyword) {
        List<Book> books = bookService.searchBooks(keyword);
        return ApiResponse.success(books);
    }

    // 添加图书
    @PostMapping
    public ApiResponse<String> addBook(@RequestBody Book book) {
        boolean success = bookService.saveBook(book);
        if (success) {
            return ApiResponse.success("添加成功");
        } else {
            return ApiResponse.error("添加失败");
        }
    }

    // 更新图书
    @PutMapping("/{id}")
    public ApiResponse<String> updateBook(@PathVariable Integer id, @RequestBody Book book) {
        book.setId(id);
        boolean success = bookService.updateBook(book);
        if (success) {
            return ApiResponse.success("更新成功");
        } else {
            return ApiResponse.error("更新失败");
        }
    }

    // 删除图书
    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteBook(@PathVariable Integer id) {
        boolean success = bookService.deleteBook(id);
        if (success) {
            return ApiResponse.success("删除成功");
        } else {
            return ApiResponse.error("删除失败");
        }
    }
}