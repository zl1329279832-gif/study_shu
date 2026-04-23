package com.library.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.library.entity.Menu;

import java.util.List;

public interface MenuService extends IService<Menu> {
    List<Menu> getMenuTree();
}
