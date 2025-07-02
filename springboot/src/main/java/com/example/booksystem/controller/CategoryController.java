package com.example.booksystem.controller;

import com.example.booksystem.model.ApiResponse;
import com.example.booksystem.model.CategoryLevel1;
import com.example.booksystem.model.CategoryLevel2;
import com.example.booksystem.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    // 获取所有一级分类
    @GetMapping("/level1")
    public ApiResponse<List<CategoryLevel1>> getAllLevel1Categories() {
        List<CategoryLevel1> categories = categoryService.getAllLevel1Categories();
        return ApiResponse.success(categories);
    }

    // 获取所有二级分类
    @GetMapping("/level2")
    public ApiResponse<List<CategoryLevel2>> getAllLevel2Categories() {
        List<CategoryLevel2> categories = categoryService.getAllLevel2Categories();
        return ApiResponse.success(categories);
    }

    // 根据父分类ID获取二级分类
    @GetMapping("/level2/parent/{parentId}")
    public ApiResponse<List<CategoryLevel2>> getLevel2CategoriesByParentId(@PathVariable Integer parentId) {
        List<CategoryLevel2> categories = categoryService.getLevel2CategoriesByParentId(parentId);
        return ApiResponse.success(categories);
    }

    // 添加一级分类
    @PostMapping("/level1")
    public ApiResponse<String> addLevel1Category(@RequestBody CategoryLevel1 category) {
        boolean success = categoryService.saveLevel1Category(category);
        if (success) {
            return ApiResponse.success("添加成功");
        } else {
            return ApiResponse.error("添加失败");
        }
    }

    // 添加二级分类
    @PostMapping("/level2")
    public ApiResponse<String> addLevel2Category(@RequestBody CategoryLevel2 category) {
        boolean success = categoryService.saveLevel2Category(category);
        if (success) {
            return ApiResponse.success("添加成功");
        } else {
            return ApiResponse.error("添加失败");
        }
    }

    // 更新一级分类
    @PutMapping("/level1/{id}")
    public ApiResponse<String> updateLevel1Category(@PathVariable Integer id, @RequestBody CategoryLevel1 category) {
        category.setId(id);
        boolean success = categoryService.updateLevel1Category(category);
        if (success) {
            return ApiResponse.success("更新成功");
        } else {
            return ApiResponse.error("更新失败");
        }
    }

    // 更新二级分类
    @PutMapping("/level2/{id}")
    public ApiResponse<String> updateLevel2Category(@PathVariable Integer id, @RequestBody CategoryLevel2 category) {
        category.setId(id);
        boolean success = categoryService.updateLevel2Category(category);
        if (success) {
            return ApiResponse.success("更新成功");
        } else {
            return ApiResponse.error("更新失败");
        }
    }

    // 删除一级分类
    @DeleteMapping("/level1/{id}")
    public ApiResponse<String> deleteLevel1Category(@PathVariable Integer id) {
        boolean success = categoryService.deleteLevel1Category(id);
        if (success) {
            return ApiResponse.success("删除成功");
        } else {
            return ApiResponse.error("删除失败");
        }
    }

    // 删除二级分类
    @DeleteMapping("/level2/{id}")
    public ApiResponse<String> deleteLevel2Category(@PathVariable Integer id) {
        boolean success = categoryService.deleteLevel2Category(id);
        if (success) {
            return ApiResponse.success("删除成功");
        } else {
            return ApiResponse.error("删除失败");
        }
    }
}