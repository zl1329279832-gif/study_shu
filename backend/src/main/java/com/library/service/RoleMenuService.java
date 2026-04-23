package com.library.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.library.entity.RoleMenu;

import java.util.List;

public interface RoleMenuService extends IService<RoleMenu> {

    List<Long> getMenuIdsByRoleId(Long roleId);

    void assignRoleMenus(Long roleId, List<Long> menuIds);
}
