package com.library.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.library.common.Result;
import com.library.entity.User;
import com.library.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/list")
    public Result<List<User>> getUserList() {
        List<User> list = userService.list();
        return Result.success(list);
    }

    @GetMapping("/{id}")
    public Result<User> getUserById(@PathVariable Long id) {
        User user = userService.getById(id);
        return Result.success(user);
    }

    @PostMapping
    public Result<String> addUser(@RequestBody User user) {
        user.setPassword(passwordEncoder.encode("123456"));
        userService.save(user);
        log.info("添加用户: {}", user.getUsername());
        return Result.success("添加成功");
    }

    @PutMapping
    public Result<String> updateUser(@RequestBody User user) {
        userService.updateById(user);
        log.info("更新用户: {}", user.getUsername());
        return Result.success("更新成功");
    }

    @DeleteMapping("/{id}")
    public Result<String> deleteUser(@PathVariable Long id) {
        userService.removeById(id);
        log.info("删除用户: {}", id);
        return Result.success("删除成功");
    }
}
