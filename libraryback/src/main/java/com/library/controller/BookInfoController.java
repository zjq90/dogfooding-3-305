package com.library.controller;

import com.library.common.BusinessException;
import com.library.common.PageResult;
import com.library.common.Result;
import com.library.entity.BookInfo;
import com.library.service.BookInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 图书信息控制器
 * 提供图书的增删改查、统计等功能
 */
@Slf4j
@RestController
@RequestMapping("/book")
public class BookInfoController {

    @Autowired
    private BookInfoService bookInfoService;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private void checkAdmin(HttpServletRequest request) {
        Integer role = (Integer) request.getAttribute("role");
        if (role == null || role != 1) {
            throw new BusinessException("权限不足，只有管理员可以执行此操作");
        }
    }
    
    private void clearBookCache(Long bookId) {
        if (bookId != null) {
            redisTemplate.delete("book:" + bookId);
        }
        Set<String> keys = redisTemplate.keys("stats:book*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    /**
     * 分页查询图书列表
     */
    @GetMapping("/page")
    public Result<PageResult<BookInfo>> getBookPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId) {
        
        PageResult<BookInfo> result = bookInfoService.getBookPage(page, size, keyword, categoryId);
        return Result.success(result);
    }

    /**
     * 根据ID获取图书详情
     */
    @GetMapping("/{id}")
    public Result<BookInfo> getBookById(@PathVariable Long id) {
        BookInfo book = bookInfoService.getBookDetail(id);
        if (book != null) {
            return Result.success(book);
        }
        return Result.error("图书不存在");
    }

    /**
     * 新增图书
     */
    @PostMapping
    public Result<Void> addBook(HttpServletRequest request, @RequestBody BookInfo book) {
        checkAdmin(request);
        if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {
            return Result.error("图书名称不能为空");
        }
        if (book.getIsbn() == null || book.getIsbn().trim().isEmpty()) {
            return Result.error("ISBN不能为空");
        }
        if (book.getAvailableQuantity() == null) {
            book.setAvailableQuantity(book.getTotalQuantity() != null ? book.getTotalQuantity() : 0);
        }
        log.info("新增图书: {}", book.getTitle());
        boolean success = bookInfoService.save(book);
        if (success) {
            clearBookCache(null);
            log.info("图书添加成功: {}", book.getTitle());
            return Result.success("添加成功");
        }
        return Result.error("添加失败");
    }

    /**
     * 更新图书信息
     */
    @PutMapping("/{id}")
    public Result<Void> updateBook(HttpServletRequest request, @PathVariable Long id, @RequestBody BookInfo book) {
        checkAdmin(request);
        log.info("更新图书信息: {}", id);
        book.setId(id);
        boolean success = bookInfoService.updateById(book);
        if (success) {
            clearBookCache(id);
            log.info("图书更新成功: {}", id);
            return Result.success("更新成功");
        }
        return Result.error("更新失败");
    }

    /**
     * 删除图书
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteBook(HttpServletRequest request, @PathVariable Long id) {
        checkAdmin(request);
        log.info("删除图书: {}", id);
        boolean success = bookInfoService.removeById(id);
        if (success) {
            clearBookCache(id);
            log.info("图书删除成功: {}", id);
            return Result.success("删除成功");
        }
        return Result.error("删除失败");
    }

    /**
     * 获取图书分类统计
     */
    @GetMapping("/stats/category")
    public Result<List<Map<String, Object>>> getCategoryStats() {
        List<Map<String, Object>> stats = bookInfoService.getCategoryStats();
        return Result.success(stats);
    }

    /**
     * 获取图书数量统计
     */
    @GetMapping("/count")
    public Result<Map<String, Long>> getBookCount() {
        Map<String, Long> result = new java.util.HashMap<>();
        result.put("bookCount", bookInfoService.getBookCount());
        result.put("totalQuantity", bookInfoService.getTotalBookQuantity());
        return Result.success(result);
    }
}
