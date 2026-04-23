package com.library.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.library.common.Result;
import com.library.entity.Book;
import com.library.service.BookService;
import com.library.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/book")
public class BookController {

    @Autowired
    private BookService bookService;

    @Autowired
    private UserService userService;

    @GetMapping("/list")
    public Result<List<Book>> getBookList() {
        List<Book> list = bookService.getBookList();
        return Result.success(list);
    }

    @GetMapping("/{id}")
    public Result<Book> getBookById(@PathVariable Long id) {
        Book book = bookService.getById(id);
        return Result.success(book);
    }

    @PostMapping
    public Result<String> addBook(@RequestBody Book book) {
        bookService.save(book);
        log.info("添加图书: {}", book.getBookName());
        return Result.success("添加成功");
    }

    @PutMapping
    public Result<String> updateBook(@RequestBody Book book) {
        bookService.updateById(book);
        log.info("更新图书: {}", book.getBookName());
        return Result.success("更新成功");
    }

    @DeleteMapping("/{id}")
    public Result<String> deleteBook(@PathVariable Long id) {
        bookService.removeById(id);
        log.info("删除图书: {}", id);
        return Result.success("删除成功");
    }

    @PostMapping("/borrow/{id}")
    public Result<String> borrowBook(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        LambdaQueryWrapper<com.library.entity.User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(com.library.entity.User::getUsername, username);
        com.library.entity.User user = userService.getOne(wrapper);
        
        bookService.borrowBook(id, user.getId());
        return Result.success("借阅成功");
    }
}
