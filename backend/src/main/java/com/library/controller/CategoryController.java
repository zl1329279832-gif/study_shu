package com.library.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.library.common.Result;
import com.library.entity.Category;
import com.library.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/list")
    public Result<List<Category>> getCategoryList() {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Category::getSortOrder);
        List<Category> list = categoryService.list(wrapper);
        return Result.success(list);
    }

    @GetMapping("/{id}")
    public Result<Category> getCategoryById(@PathVariable Long id) {
        Category category = categoryService.getById(id);
        return Result.success(category);
    }

    @PostMapping
    public Result<String> addCategory(@RequestBody Category category) {
        categoryService.save(category);
        log.info("添加分类: {}", category.getCategoryName());
        return Result.success("添加成功");
    }

    @PutMapping
    public Result<String> updateCategory(@RequestBody Category category) {
        categoryService.updateById(category);
        log.info("更新分类: {}", category.getCategoryName());
        return Result.success("更新成功");
    }

    @DeleteMapping("/{id}")
    public Result<String> deleteCategory(@PathVariable Long id) {
        categoryService.removeById(id);
        log.info("删除分类: {}", id);
        return Result.success("删除成功");
    }
}
