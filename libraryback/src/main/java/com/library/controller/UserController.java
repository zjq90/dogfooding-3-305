package com.library.controller;

import com.library.common.BusinessException;
import com.library.common.PageResult;
import com.library.common.Result;
import com.library.entity.User;
import com.library.service.UserService;
import com.library.util.PasswordUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户管理控制器
 * 提供用户的增删改查、状态管理等功能
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    private void checkAdmin(HttpServletRequest request) {
        Integer role = (Integer) request.getAttribute("role");
        if (role == null || role != 1) {
            throw new BusinessException("权限不足，只有管理员可以执行此操作");
        }
    }

    /**
     * 分页查询用户列表
     */
    @GetMapping("/page")
    public Result<PageResult<User>> getUserPage(
            HttpServletRequest request,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword) {
        checkAdmin(request);
        PageResult<User> result = userService.getUserPage(page, size, keyword);
        return Result.success(result);
    }

    /**
     * 根据ID获取用户详情
     */
    @GetMapping("/{id}")
    public Result<User> getUserById(HttpServletRequest request, @PathVariable Long id) {
        Integer role = (Integer) request.getAttribute("role");
        Long userId = (Long) request.getAttribute("userId");
        if (role != 1 && !userId.equals(id)) {
            throw new BusinessException("权限不足");
        }
        User user = userService.getById(id);
        if (user != null) {
            user.setPassword(null);
            return Result.success(user);
        }
        return Result.error("用户不存在");
    }

    /**
     * 新增用户
     */
    @PostMapping
    public Result<Void> addUser(HttpServletRequest request, @RequestBody User user) {
        checkAdmin(request);
        log.info("新增用户: {}", user.getUsername());
        User existUser = userService.getByUsername(user.getUsername());
        if (existUser != null) {
            return Result.error("用户名已存在");
        }
        if (user.getPassword() == null || user.getPassword().length() < 6) {
            return Result.error("密码长度不能少于6位");
        }
        user.setPassword(PasswordUtil.encode(user.getPassword()));
        user.setStatus(user.getStatus() == null ? 1 : user.getStatus());
        boolean success = userService.save(user);
        if (success) {
            log.info("用户添加成功: {}", user.getUsername());
            return Result.success("添加成功");
        }
        return Result.error("添加失败");
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/{id}")
    public Result<Void> updateUser(HttpServletRequest request, @PathVariable Long id, @RequestBody User user) {
        checkAdmin(request);
        log.info("更新用户信息: {}", id);
        user.setId(id);
        if (user.getPassword() != null && user.getPassword().length() > 0 && user.getPassword().length() < 32) {
            user.setPassword(PasswordUtil.encode(user.getPassword()));
        }
        boolean success = userService.updateById(user);
        if (success) {
            log.info("用户更新成功: {}", id);
            return Result.success("更新成功");
        }
        return Result.error("更新失败");
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(HttpServletRequest request, @PathVariable Long id) {
        checkAdmin(request);
        log.info("删除用户: {}", id);
        boolean success = userService.removeById(id);
        if (success) {
            log.info("用户删除成功: {}", id);
            return Result.success("删除成功");
        }
        return Result.error("删除失败");
    }

    /**
     * 更新用户状态
     */
    @PutMapping("/{id}/status")
    public Result<Void> updateUserStatus(HttpServletRequest request, @PathVariable Long id, @RequestParam Integer status) {
        checkAdmin(request);
        log.info("更新用户状态: {}, 状态: {}", id, status);
        User user = new User();
        user.setId(id);
        user.setStatus(status);
        boolean success = userService.updateById(user);
        if (success) {
            return Result.success("状态更新成功");
        }
        return Result.error("状态更新失败");
    }

    /**
     * 修改用户密码
     */
    @PostMapping("/{id}/password")
    public Result<Void> updatePassword(
            @PathVariable Long id,
            @RequestParam String oldPassword,
            @RequestParam String newPassword) {
        
        log.info("修改用户密码: {}", id);
        boolean success = userService.updatePassword(id, oldPassword, newPassword);
        if (success) {
            log.info("密码修改成功: {}", id);
            return Result.success("密码修改成功");
        }
        return Result.error("密码修改失败");
    }
}
