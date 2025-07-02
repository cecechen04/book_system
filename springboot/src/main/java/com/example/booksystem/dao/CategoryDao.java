package com.example.booksystem.dao;

import com.example.booksystem.model.CategoryLevel1;
import com.example.booksystem.model.CategoryLevel2;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class CategoryDao extends JdbcDaoSupport {

    public CategoryDao(DataSource dataSource) {
        setDataSource(dataSource);
    }

    // 一级分类操作
    public List<CategoryLevel1> findAllLevel1() {
        String sql = "SELECT * FROM category_level1 ORDER BY id";
        return getJdbcTemplate().query(sql, new BeanPropertyRowMapper<>(CategoryLevel1.class));
    }

    public CategoryLevel1 findLevel1ById(Integer id) {
        String sql = "SELECT * FROM category_level1 WHERE id = ?";
        List<CategoryLevel1> categories = getJdbcTemplate().query(sql, new BeanPropertyRowMapper<>(CategoryLevel1.class), id);
        return categories.isEmpty() ? null : categories.get(0);
    }

    public int saveLevel1(CategoryLevel1 category) {
        String sql = "INSERT INTO category_level1 (name) VALUES (?)";
        return getJdbcTemplate().update(sql, category.getName());
    }

    public int updateLevel1(CategoryLevel1 category) {
        String sql = "UPDATE category_level1 SET name = ? WHERE id = ?";
        return getJdbcTemplate().update(sql, category.getName(), category.getId());
    }

    public int deleteLevel1(Integer id) {
        String sql = "DELETE FROM category_level1 WHERE id = ?";
        return getJdbcTemplate().update(sql, id);
    }

    // 二级分类操作
    public List<CategoryLevel2> findAllLevel2() {
        String sql = "SELECT * FROM category_level2 ORDER BY parent_id, id";
        return getJdbcTemplate().query(sql, new BeanPropertyRowMapper<>(CategoryLevel2.class));
    }

    public List<CategoryLevel2> findLevel2ByParentId(Integer parentId) {
        String sql = "SELECT * FROM category_level2 WHERE parent_id = ? ORDER BY id";
        return getJdbcTemplate().query(sql, new BeanPropertyRowMapper<>(CategoryLevel2.class), parentId);
    }

    public CategoryLevel2 findLevel2ById(Integer id) {
        String sql = "SELECT * FROM category_level2 WHERE id = ?";
        List<CategoryLevel2> categories = getJdbcTemplate().query(sql, new BeanPropertyRowMapper<>(CategoryLevel2.class), id);
        return categories.isEmpty() ? null : categories.get(0);
    }

    public int saveLevel2(CategoryLevel2 category) {
        String sql = "INSERT INTO category_level2 (name, parent_id) VALUES (?, ?)";
        return getJdbcTemplate().update(sql, category.getName(), category.getParentId());
    }

    public int updateLevel2(CategoryLevel2 category) {
        String sql = "UPDATE category_level2 SET name = ?, parent_id = ? WHERE id = ?";
        return getJdbcTemplate().update(sql, category.getName(), category.getParentId(), category.getId());
    }

    public int deleteLevel2(Integer id) {
        String sql = "DELETE FROM category_level2 WHERE id = ?";
        return getJdbcTemplate().update(sql, id);
    }
}