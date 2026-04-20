package com.library.controller;

import com.library.common.BusinessException;
import com.library.common.PageResult;
import com.library.common.Result;
import com.library.entity.BorrowRecord;
import com.library.service.BorrowRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 借阅记录控制器
 * 提供图书借阅、归还、记录查询等功能
 */
@Slf4j
@RestController
@RequestMapping("/borrow")
public class BorrowRecordController {

    @Autowired
    private BorrowRecordService borrowRecordService;

    /**
     * 分页查询借阅记录
     */
    @GetMapping("/page")
    public Result<PageResult<BorrowRecord>> getBorrowPage(
            HttpServletRequest request,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long bookId,
            @RequestParam(required = false) Integer status) {
        Integer role = (Integer) request.getAttribute("role");
        Long currentUserId = (Long) request.getAttribute("userId");
        
        if (role != null && role == 1) {
            PageResult<BorrowRecord> result = borrowRecordService.getBorrowPage(page, size, userId, bookId, status);
            return Result.success(result);
        } else {
            PageResult<BorrowRecord> result = borrowRecordService.getBorrowPage(page, size, currentUserId, bookId, status);
            return Result.success(result);
        }
    }

    /**
     * 根据ID获取借阅记录详情
     */
    @GetMapping("/{id}")
    public Result<BorrowRecord> getBorrowById(@PathVariable Long id) {
        BorrowRecord record = borrowRecordService.getBorrowDetail(id);
        if (record != null) {
            return Result.success(record);
        }
        return Result.error("记录不存在");
    }

    /**
     * 借阅图书
     */
    @PostMapping
    public Result<Void> borrowBook(
            HttpServletRequest request,
            @RequestParam Long bookId,
            @RequestParam(defaultValue = "30") Integer borrowDays) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error("用户未登录");
        }
        if (bookId == null) {
            return Result.error("请选择图书");
        }
        if (borrowDays == null || borrowDays < 1 || borrowDays > 60) {
            return Result.error("借阅天数必须在1-60天之间");
        }
        log.info("用户 {} 借阅图书 {}, 借阅天数: {}天", userId, bookId, borrowDays);
        
        boolean success = borrowRecordService.borrowBook(userId, bookId, borrowDays);
        if (success) {
            log.info("借阅成功: 用户 {}, 图书 {}", userId, bookId);
            return Result.success("借阅成功");
        }
        return Result.error("借阅失败");
    }

    /**
     * 归还图书
     */
    @PutMapping("/{id}/return")
    public Result<Void> returnBook(HttpServletRequest request, @PathVariable Long id) {
        if (id == null) {
            return Result.error("记录ID不能为空");
        }
        Integer role = (Integer) request.getAttribute("role");
        if (role == null || role != 1) {
            throw new BusinessException("权限不足，只有管理员可以执行归还操作");
        }
        log.info("归还图书，记录ID: {}", id);
        
        boolean success = borrowRecordService.returnBook(id);
        if (success) {
            log.info("归还成功，记录ID: {}", id);
            return Result.success("归还成功");
        }
        return Result.error("归还失败");
    }

    /**
     * 获取当前用户的借阅记录
     */
    @GetMapping("/my")
    public Result<PageResult<BorrowRecord>> getMyBorrows(
            HttpServletRequest request,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error("用户未登录");
        }
        PageResult<BorrowRecord> result = borrowRecordService.getBorrowPage(page, size, userId, null, status);
        return Result.success(result);
    }

    /**
     * 获取月度借阅统计
     */
    @GetMapping("/stats/monthly")
    public Result<List<Map<String, Object>>> getMonthlyStats() {
        List<Map<String, Object>> stats = borrowRecordService.getMonthlyBorrowStats();
        return Result.success(stats);
    }

    /**
     * 获取热门图书统计
     */
    @GetMapping("/stats/hot")
    public Result<List<Map<String, Object>>> getHotBooks() {
        List<Map<String, Object>> hotBooks = borrowRecordService.getHotBooks();
        return Result.success(hotBooks);
    }

    /**
     * 获取借阅数量统计
     */
    @GetMapping("/count")
    public Result<Map<String, Object>> getBorrowCount() {
        Map<String, Object> result = new java.util.HashMap<>();
        result.put("totalBorrowCount", borrowRecordService.getTotalBorrowCount());
        result.put("overdueCount", borrowRecordService.getOverdueCount());
        return Result.success(result);
    }
}
