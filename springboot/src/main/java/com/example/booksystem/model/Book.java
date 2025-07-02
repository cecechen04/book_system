package com.example.booksystem.model;

import java.math.BigDecimal;

public class Book {
    private Integer id;
    private String title;
    private String author;
    private BigDecimal price;
    private Integer stock;
    private Integer categoryId;
    private String coverUrl;
    private String description;

    public Book() {}

    public Book(String title, String author, BigDecimal price, Integer stock, Integer categoryId) {
        this.title = title;
        this.author = author;
        this.price = price;
        this.stock = stock;
        this.categoryId = categoryId;
    }

    public Book(String title, String author, BigDecimal price, Integer stock, Integer categoryId, String coverUrl, String description) {
        this.title = title;
        this.author = author;
        this.price = price;
        this.stock = stock;
        this.categoryId = categoryId;
        this.coverUrl = coverUrl;
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}