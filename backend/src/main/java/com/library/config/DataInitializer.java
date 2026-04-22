package com.library.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.library.entity.*;
import com.library.mapper.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private MenuMapper menuMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private RoleMenuMapper roleMenuMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        log.info("开始初始化数据...");
        
        initRoles();
        initMenus();
        initRoleMenus();
        initUsers();
        initUserRoles();
        
        log.info("数据初始化完成");
    }

    private void initRoles() {
        LambdaQueryWrapper<Role> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Role::getRoleCode, "ADMIN", "MANAGER", "USER");
        Long count = roleMapper.selectCount(queryWrapper);
        
        if (count >= 3) {
            log.info("角色数据已存在，跳过初始化");
            return;
        }

        log.info("初始化角色数据...");
        
        Role admin = new Role();
        admin.setRoleName("超级管理员");
        admin.setRoleCode("ADMIN");
        admin.setDescription("系统超级管理员，拥有所有权限");
        admin.setStatus(1);
        roleMapper.insert(admin);

        Role manager = new Role();
        manager.setRoleName("管理员");
        manager.setRoleCode("MANAGER");
        manager.setDescription("普通管理员，拥有图书管理权限");
        manager.setStatus(1);
        roleMapper.insert(manager);

        Role user = new Role();
        user.setRoleName("普通用户");
        user.setRoleCode("USER");
        user.setDescription("普通用户，只能查询和借阅图书");
        user.setStatus(1);
        roleMapper.insert(user);
    }

    private void initMenus() {
        LambdaQueryWrapper<Menu> queryWrapper = new LambdaQueryWrapper<>();
        Long count = menuMapper.selectCount(queryWrapper);
        
        if (count > 0) {
            log.info("菜单数据已存在，跳过初始化");
            return;
        }

        log.info("初始化菜单数据...");
        
        List<Menu> menus = new ArrayList<>();
        
        menus.add(createMenu("系统管理", 0L, "/system", "", "Setting", 1, 1));
        menus.add(createMenu("用户管理", 1L, "/system/user", "system/user/index", "User", 1, 1));
        menus.add(createMenu("角色管理", 1L, "/system/role", "system/role/index", "Stamp", 2, 1));
        menus.add(createMenu("菜单管理", 1L, "/system/menu", "system/menu/index", "Menu", 3, 1));
        menus.add(createMenu("图书管理", 0L, "/book", "", "Reading", 2, 1));
        menus.add(createMenu("图书列表", 5L, "/book/list", "book/list/index", "List", 1, 1));
        menus.add(createMenu("图书分类", 5L, "/book/category", "book/category/index", "Grid", 2, 1));
        menus.add(createMenu("借阅管理", 0L, "/borrow", "", "Tickets", 3, 1));
        menus.add(createMenu("借阅记录", 8L, "/borrow/record", "borrow/record/index", "Document", 1, 1));
        menus.add(createMenu("我的借阅", 8L, "/borrow/my", "borrow/my/index", "User", 2, 1));

        for (Menu menu : menus) {
            menuMapper.insert(menu);
        }
    }

    private Menu createMenu(String name, Long parentId, String path, String component, String icon, int sort, int type) {
        Menu menu = new Menu();
        menu.setMenuName(name);
        menu.setParentId(parentId);
        menu.setPath(path);
        menu.setComponent(component);
        menu.setIcon(icon);
        menu.setSortOrder(sort);
        menu.setMenuType(type);
        menu.setVisible(1);
        menu.setStatus(1);
        return menu;
    }

    private void initRoleMenus() {
        LambdaQueryWrapper<RoleMenu> queryWrapper = new LambdaQueryWrapper<>();
        Long count = roleMenuMapper.selectCount(queryWrapper);
        
        if (count > 0) {
            log.info("角色菜单关联数据已存在，跳过初始化");
            return;
        }

        log.info("初始化角色菜单关联数据...");
        
        List<Menu> allMenus = menuMapper.selectList(null);
        
        List<Role> roles = roleMapper.selectList(null);
        Long adminId = null;
        Long managerId = null;
        Long userId = null;
        
        for (Role role : roles) {
            switch (role.getRoleCode()) {
                case "ADMIN": adminId = role.getId(); break;
                case "MANAGER": managerId = role.getId(); break;
                case "USER": userId = role.getId(); break;
            }
        }
        
        for (Menu menu : allMenus) {
            RoleMenu roleMenu = new RoleMenu();
            roleMenu.setRoleId(adminId);
            roleMenu.setMenuId(menu.getId());
            roleMenuMapper.insert(roleMenu);
        }
        
        for (Menu menu : allMenus) {
            if (menu.getPath().startsWith("/book") || menu.getPath().startsWith("/borrow")) {
                RoleMenu roleMenu = new RoleMenu();
                roleMenu.setRoleId(managerId);
                roleMenu.setMenuId(menu.getId());
                roleMenuMapper.insert(roleMenu);
            }
        }
        
        for (Menu menu : allMenus) {
            if (menu.getPath().startsWith("/book") || 
                "/borrow/my".equals(menu.getPath()) ||
                "/borrow".equals(menu.getPath())) {
                RoleMenu roleMenu = new RoleMenu();
                roleMenu.setRoleId(userId);
                roleMenu.setMenuId(menu.getId());
                roleMenuMapper.insert(roleMenu);
            }
        }
    }

    private void initUsers() {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(User::getUsername, "admin", "manager", "user");
        Long count = userMapper.selectCount(queryWrapper);
        
        if (count >= 3) {
            log.info("用户数据已存在，跳过初始化");
            return;
        }

        log.info("初始化用户数据...");
        String password = passwordEncoder.encode("123456");
        log.info("密码加密完成，明文: 123456");

        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(password);
        admin.setRealName("超级管理员");
        admin.setPhone("13800138000");
        admin.setEmail("admin@library.com");
        admin.setStatus(1);
        userMapper.insert(admin);

        User manager = new User();
        manager.setUsername("manager");
        manager.setPassword(password);
        manager.setRealName("管理员");
        manager.setPhone("13800138001");
        manager.setEmail("manager@library.com");
        manager.setStatus(1);
        userMapper.insert(manager);

        User user = new User();
        user.setUsername("user");
        user.setPassword(password);
        user.setRealName("普通用户");
        user.setPhone("13800138002");
        user.setEmail("user@library.com");
        user.setStatus(1);
        userMapper.insert(user);
    }

    private void initUserRoles() {
        LambdaQueryWrapper<UserRole> queryWrapper = new LambdaQueryWrapper<>();
        Long count = userRoleMapper.selectCount(queryWrapper);
        
        if (count > 0) {
            log.info("用户角色关联数据已存在，跳过初始化");
            return;
        }

        log.info("初始化用户角色关联数据...");
        
        List<User> users = userMapper.selectList(null);
        List<Role> roles = roleMapper.selectList(null);
        
        Long adminUserId = null;
        Long managerUserId = null;
        Long normalUserId = null;
        
        Long adminRoleId = null;
        Long managerRoleId = null;
        Long userRoleId = null;
        
        for (User u : users) {
            switch (u.getUsername()) {
                case "admin": adminUserId = u.getId(); break;
                case "manager": managerUserId = u.getId(); break;
                case "user": normalUserId = u.getId(); break;
            }
        }
        
        for (Role r : roles) {
            switch (r.getRoleCode()) {
                case "ADMIN": adminRoleId = r.getId(); break;
                case "MANAGER": managerRoleId = r.getId(); break;
                case "USER": userRoleId = r.getId(); break;
            }
        }
        
        if (adminUserId != null && adminRoleId != null) {
            UserRole ur = new UserRole();
            ur.setUserId(adminUserId);
            ur.setRoleId(adminRoleId);
            userRoleMapper.insert(ur);
        }
        
        if (managerUserId != null && managerRoleId != null) {
            UserRole ur = new UserRole();
            ur.setUserId(managerUserId);
            ur.setRoleId(managerRoleId);
            userRoleMapper.insert(ur);
        }
        
        if (normalUserId != null && userRoleId != null) {
            UserRole ur = new UserRole();
            ur.setUserId(normalUserId);
            ur.setRoleId(userRoleId);
            userRoleMapper.insert(ur);
        }
    }

}
