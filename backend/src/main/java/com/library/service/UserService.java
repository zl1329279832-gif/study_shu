package com.library.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.library.dto.LoginDTO;
import com.library.entity.User;
import com.library.vo.LoginVO;

public interface UserService extends IService<User> {

    LoginVO login(LoginDTO loginDTO);

    LoginVO getUserInfo(String username);

}
