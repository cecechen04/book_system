package com.example.booksystem.model;

public class CategoryLevel1 {
    private Integer id;
    private String name;

    public CategoryLevel1() {}

    public CategoryLevel1(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}