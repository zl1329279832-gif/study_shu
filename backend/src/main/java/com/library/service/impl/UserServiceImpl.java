package com.library.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.dto.LoginDTO;
import com.library.entity.Menu;
import com.library.entity.Role;
import com.library.entity.User;
import com.library.mapper.UserMapper;
import com.library.service.UserService;
import com.library.util.JwtUtil;
import com.library.vo.LoginVO;
import com.library.vo.MenuVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserMapper userMapper;

    @Override
    public LoginVO login(LoginDTO loginDTO) {
        log.info("用户登录: {}", loginDTO.getUsername());
        
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDTO.getUsername(),
                        loginDTO.getPassword()
                )
        );

        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, loginDTO.getUsername());
        User user = userMapper.selectOne(queryWrapper);

        String token = jwtUtil.generateToken(user.getUsername(), user.getId());

        return buildLoginVO(user, token);
    }

    @Override
    public LoginVO getUserInfo(String username) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, username);
        User user = userMapper.selectOne(queryWrapper);

        return buildLoginVO(user, null);
    }

    private LoginVO buildLoginVO(User user, String token) {
        LoginVO loginVO = new LoginVO();
        loginVO.setUserId(user.getId());
        loginVO.setUsername(user.getUsername());
        loginVO.setRealName(user.getRealName());
        loginVO.setToken(token);

        List<Role> roles = userMapper.selectRolesByUserId(user.getId());
        if (roles != null && !roles.isEmpty()) {
            List<String> roleCodes = roles.stream()
                    .map(Role::getRoleCode)
                    .collect(Collectors.toList());
            loginVO.setRoles(roleCodes);
        }

        List<Menu> menus = userMapper.selectMenusByUserId(user.getId());
        if (menus != null && !menus.isEmpty()) {
            List<MenuVO> menuVOList = buildMenuTree(menus);
            loginVO.setMenus(menuVOList);
        }

        return loginVO;
    }

    private List<MenuVO> buildMenuTree(List<Menu> menus) {
        List<MenuVO> menuVOList = menus.stream()
                .map(menu -> {
                    MenuVO vo = new MenuVO();
                    BeanUtil.copyProperties(menu, vo);
                    return vo;
                })
                .collect(Collectors.toList());

        Map<Long, List<MenuVO>> parentMap = menuVOList.stream()
                .collect(Collectors.groupingBy(MenuVO::getParentId));

        menuVOList.forEach(menu -> {
            menu.setChildren(parentMap.getOrDefault(menu.getId(), new ArrayList<>()));
        });

        return menuVOList.stream()
                .filter(menu -> menu.getParentId() == 0)
                .collect(Collectors.toList());
    }

}
