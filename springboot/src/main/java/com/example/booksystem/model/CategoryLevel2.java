package com.example.booksystem.model;

public class CategoryLevel2 {
    private Integer id;
    private String name;
    private Integer parentId;

    public CategoryLevel2() {}

    public CategoryLevel2(String name, Integer parentId) {
        this.name = name;
        this.parentId = parentId;
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

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }
}