package com.example.booksystem.service;

import com.example.booksystem.dao.CategoryDao;
import com.example.booksystem.model.CategoryLevel1;
import com.example.booksystem.model.CategoryLevel2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryDao categoryDao;

    // 一级分类操作
    public List<CategoryLevel1> getAllLevel1Categories() {
        return categoryDao.findAllLevel1();
    }

    public CategoryLevel1 getLevel1CategoryById(Integer id) {
        return categoryDao.findLevel1ById(id);
    }

    public boolean saveLevel1Category(CategoryLevel1 category) {
        return categoryDao.saveLevel1(category) > 0;
    }

    public boolean updateLevel1Category(CategoryLevel1 category) {
        return categoryDao.updateLevel1(category) > 0;
    }

    public boolean deleteLevel1Category(Integer id) {
        return categoryDao.deleteLevel1(id) > 0;
    }

    // 二级分类操作
    public List<CategoryLevel2> getAllLevel2Categories() {
        return categoryDao.findAllLevel2();
    }

    public List<CategoryLevel2> getLevel2CategoriesByParentId(Integer parentId) {
        return categoryDao.findLevel2ByParentId(parentId);
    }

    public CategoryLevel2 getLevel2CategoryById(Integer id) {
        return categoryDao.findLevel2ById(id);
    }

    public boolean saveLevel2Category(CategoryLevel2 category) {
        return categoryDao.saveLevel2(category) > 0;
    }

    public boolean updateLevel2Category(CategoryLevel2 category) {
        return categoryDao.updateLevel2(category) > 0;
    }

    public boolean deleteLevel2Category(Integer id) {
        return categoryDao.deleteLevel2(id) > 0;
    }
}