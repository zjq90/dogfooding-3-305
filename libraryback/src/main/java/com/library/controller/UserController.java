package com.library.controller;

import com.library.common.PageResult;
import com.library.common.Result;
import com.library.entity.User;
import com.library.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    /**
     * 分页查询用户列表
     */
    @GetMapping("/page")
    public Result<PageResult<User>> getUserPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword) {
        
        PageResult<User> result = userService.getUserPage(page, size, keyword);
        return Result.success(result);
    }

    /**
     * 根据ID获取用户详情
     */
    @GetMapping("/{id}")
    public Result<User> getUserById(@PathVariable Long id) {
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
    public Result<Void> addUser(@RequestAttribute Integer role, @RequestBody User user) {
        // 只有管理员可以新增用户
        if (role == null || role != 1) {
            return Result.error(403, "无权操作，需要管理员权限");
        }
        // 禁止创建管理员用户
        if (user.getRole() != null && user.getRole() == 1) {
            return Result.error(403, "无权创建管理员用户");
        }
        // 检查用户名是否已存在
        User existUser = userService.getByUsername(user.getUsername());
        if (existUser != null) {
            return Result.error("用户名已存在");
        }
        log.info("新增用户: {}", user.getUsername());
        boolean success = userService.addUser(user);
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
    public Result<Void> updateUser(@RequestAttribute Integer role, @PathVariable Long id, @RequestBody User user) {
        // 只有管理员可以更新用户信息
        if (role == null || role != 1) {
            return Result.error(403, "无权操作，需要管理员权限");
        }
        // 禁止修改用户角色为管理员
        if (user.getRole() != null && user.getRole() == 1) {
            return Result.error(403, "无权将用户设为管理员");
        }
        log.info("更新用户信息: {}", id);
        user.setId(id);
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
    public Result<Void> deleteUser(@RequestAttribute Integer role, @PathVariable Long id) {
        // 只有管理员可以删除用户
        if (role == null || role != 1) {
            return Result.error(403, "无权操作，需要管理员权限");
        }
        // 禁止删除管理员用户
        User targetUser = userService.getById(id);
        if (targetUser != null && targetUser.getRole() != null && targetUser.getRole() == 1) {
            return Result.error(403, "无权删除管理员用户");
        }
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
    public Result<Void> updateUserStatus(@RequestAttribute Integer role, @PathVariable Long id, @RequestParam Integer status) {
        // 只有管理员可以更新用户状态
        if (role == null || role != 1) {
            return Result.error(403, "无权操作，需要管理员权限");
        }
        // 禁止修改管理员用户状态
        User targetUser = userService.getById(id);
        if (targetUser != null && targetUser.getRole() != null && targetUser.getRole() == 1) {
            return Result.error(403, "无权修改管理员用户状态");
        }
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
