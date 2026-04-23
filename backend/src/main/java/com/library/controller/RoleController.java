package com.library.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.library.common.Result;
import com.library.entity.Role;
import com.library.service.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @GetMapping("/list")
    public Result<List<Role>> getRoleList() {
        List<Role> list = roleService.list();
        return Result.success(list);
    }

    @GetMapping("/{id}")
    public Result<Role> getRoleById(@PathVariable Long id) {
        Role role = roleService.getById(id);
        return Result.success(role);
    }

    @PostMapping
    public Result<String> addRole(@RequestBody Role role) {
        roleService.save(role);
        log.info("添加角色: {}", role.getRoleName());
        return Result.success("添加成功");
    }

    @PutMapping
    public Result<String> updateRole(@RequestBody Role role) {
        roleService.updateById(role);
        log.info("更新角色: {}", role.getRoleName());
        return Result.success("更新成功");
    }

    @DeleteMapping("/{id}")
    public Result<String> deleteRole(@PathVariable Long id) {
        roleService.removeById(id);
        log.info("删除角色: {}", id);
        return Result.success("删除成功");
    }
}
