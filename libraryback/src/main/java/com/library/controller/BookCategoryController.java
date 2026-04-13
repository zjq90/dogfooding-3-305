package com.library.controller;

import com.library.common.BusinessException;
import com.library.common.Result;
import com.library.entity.BookCategory;
import com.library.service.BookCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;

/**
 * 图书分类控制器
 * 提供分类的增删改查、树形结构查询等功能
 */
@Slf4j
@RestController
@RequestMapping("/category")
public class BookCategoryController {

    @Autowired
    private BookCategoryService categoryService;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private void checkAdmin(HttpServletRequest request) {
        Integer role = (Integer) request.getAttribute("role");
        if (role == null || role != 1) {
            throw new BusinessException("权限不足，只有管理员可以执行此操作");
        }
    }
    
    private void clearCategoryCache() {
        Set<String> keys = redisTemplate.keys("categories:*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
        Set<String> nameKeys = redisTemplate.keys("category:name:*");
        if (nameKeys != null && !nameKeys.isEmpty()) {
            redisTemplate.delete(nameKeys);
        }
    }

    /**
     * 获取所有分类列表
     */
    @GetMapping("/list")
    public Result<List<BookCategory>> getAllCategories() {
        List<BookCategory> categories = categoryService.getAllCategories();
        return Result.success(categories);
    }

    /**
     * 获取分类树形结构
     */
    @GetMapping("/tree")
    public Result<List<BookCategory>> getCategoryTree() {
        List<BookCategory> categories = categoryService.getCategoryTree();
        return Result.success(categories);
    }

    /**
     * 根据ID获取分类详情
     */
    @GetMapping("/{id}")
    public Result<BookCategory> getCategoryById(@PathVariable Long id) {
        BookCategory category = categoryService.getById(id);
        if (category != null) {
            return Result.success(category);
        }
        return Result.error("分类不存在");
    }

    /**
     * 新增分类
     */
    @PostMapping
    public Result<Void> addCategory(HttpServletRequest request, @RequestBody BookCategory category) {
        checkAdmin(request);
        log.info("新增分类: {}", category.getName());
        boolean success = categoryService.save(category);
        if (success) {
            clearCategoryCache();
            log.info("分类添加成功: {}", category.getName());
            return Result.success("添加成功");
        }
        return Result.error("添加失败");
    }

    /**
     * 更新分类信息
     */
    @PutMapping("/{id}")
    public Result<Void> updateCategory(HttpServletRequest request, @PathVariable Long id, @RequestBody BookCategory category) {
        checkAdmin(request);
        log.info("更新分类信息: {}", id);
        category.setId(id);
        boolean success = categoryService.updateById(category);
        if (success) {
            clearCategoryCache();
            log.info("分类更新成功: {}", id);
            return Result.success("更新成功");
        }
        return Result.error("更新失败");
    }

    /**
     * 删除分类
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteCategory(HttpServletRequest request, @PathVariable Long id) {
        checkAdmin(request);
        log.info("删除分类: {}", id);
        boolean success = categoryService.removeById(id);
        if (success) {
            clearCategoryCache();
            log.info("分类删除成功: {}", id);
            return Result.success("删除成功");
        }
        return Result.error("删除失败");
    }
}
