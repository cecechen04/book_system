package com.example.booksystem.model;

import java.math.BigDecimal;

public class OrderDetail {
    private Integer id;
    private Integer orderId;
    private Integer bookId;
    private Integer quantity;
    private BigDecimal price;

    public OrderDetail() {}

    public OrderDetail(Integer orderId, Integer bookId, Integer quantity, BigDecimal price) {
        this.orderId = orderId;
        this.bookId = bookId;
        this.quantity = quantity;
        this.price = price;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}