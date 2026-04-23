package com.library.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.library.common.Result;
import com.library.entity.BorrowRecord;
import com.library.service.BorrowRecordService;
import com.library.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/borrow")
public class BorrowRecordController {

    @Autowired
    private BorrowRecordService borrowRecordService;

    @Autowired
    private UserService userService;

    @GetMapping("/list")
    public Result<List<BorrowRecord>> getAllRecords() {
        List<BorrowRecord> list = borrowRecordService.getAllRecords();
        return Result.success(list);
    }

    @GetMapping("/my")
    public Result<List<BorrowRecord>> getMyRecords() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        LambdaQueryWrapper<com.library.entity.User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(com.library.entity.User::getUsername, username);
        com.library.entity.User user = userService.getOne(wrapper);
        
        List<BorrowRecord> list = borrowRecordService.getMyRecords(user.getId());
        return Result.success(list);
    }

    @PostMapping("/return/{id}")
    public Result<String> returnBook(@PathVariable Long id) {
        borrowRecordService.returnBook(id);
        return Result.success("归还成功");
    }
}
