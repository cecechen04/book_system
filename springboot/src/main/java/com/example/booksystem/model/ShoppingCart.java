package com.example.booksystem.model;

public class ShoppingCart {
    private Integer id;
    private Integer userId;
    private Integer bookId;
    private Integer quantity;

    public ShoppingCart() {}

    public ShoppingCart(Integer userId, Integer bookId, Integer quantity) {
        this.userId = userId;
        this.bookId = bookId;
        this.quantity = quantity;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}