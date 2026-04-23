package com.library.controller;

import com.library.common.Result;
import com.library.entity.Menu;
import com.library.service.MenuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/menu")
public class MenuController {

    @Autowired
    private MenuService menuService;

    @GetMapping("/tree")
    public Result<List<Menu>> getMenuTree() {
        List<Menu> list = menuService.getMenuTree();
        return Result.success(list);
    }

    @GetMapping("/{id}")
    public Result<Menu> getMenuById(@PathVariable Long id) {
        Menu menu = menuService.getById(id);
        return Result.success(menu);
    }

    @PostMapping
    public Result<String> addMenu(@RequestBody Menu menu) {
        menuService.save(menu);
        log.info("添加菜单: {}", menu.getMenuName());
        return Result.success("添加成功");
    }

    @PutMapping
    public Result<String> updateMenu(@RequestBody Menu menu) {
        menuService.updateById(menu);
        log.info("更新菜单: {}", menu.getMenuName());
        return Result.success("更新成功");
    }

    @DeleteMapping("/{id}")
    public Result<String> deleteMenu(@PathVariable Long id) {
        menuService.removeById(id);
        log.info("删除菜单: {}", id);
        return Result.success("删除成功");
    }
}
