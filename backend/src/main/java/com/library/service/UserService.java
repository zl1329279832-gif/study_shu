package com.library.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.library.dto.LoginDTO;
import com.library.dto.PasswordUpdateDTO;
import com.library.dto.UserUpdateDTO;
import com.library.entity.User;
import com.library.vo.LoginVO;

public interface UserService extends IService<User> {

    LoginVO login(LoginDTO loginDTO);

    LoginVO getUserInfo(String username);

    void updateUserInfo(String username, UserUpdateDTO dto);

    void updatePassword(String username, PasswordUpdateDTO dto);

}
