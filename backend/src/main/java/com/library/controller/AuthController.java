package com.library.controller;

import com.library.common.Result;
import com.library.dto.LoginDTO;
import com.library.dto.PasswordUpdateDTO;
import com.library.dto.UserUpdateDTO;
import com.library.service.UserService;
import com.library.vo.LoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO loginDTO) {
        LoginVO loginVO = userService.login(loginDTO);
        return Result.success("登录成功", loginVO);
    }

    @GetMapping("/userinfo")
    public Result<LoginVO> getUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        LoginVO loginVO = userService.getUserInfo(username);
        return Result.success(loginVO);
    }

    @PutMapping("/userinfo")
    public Result<String> updateUserInfo(@Valid @RequestBody UserUpdateDTO dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        userService.updateUserInfo(username, dto);
        return Result.success("个人信息修改成功");
    }

    @PutMapping("/password")
    public Result<String> updatePassword(@Valid @RequestBody PasswordUpdateDTO dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        userService.updatePassword(username, dto);
        return Result.success("密码修改成功");
    }

    @PostMapping("/logout")
    public Result<String> logout() {
        return Result.success("退出成功");
    }

}
