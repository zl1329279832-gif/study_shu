package com.library.controller;

import com.library.common.Result;
import com.library.service.RoleMenuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/role-menu")
public class RoleMenuController {

    @Autowired
    private RoleMenuService roleMenuService;

    @GetMapping("/{roleId}")
    public Result<List<Long>> getRoleMenus(@PathVariable Long roleId) {
        List<Long> menuIds = roleMenuService.getMenuIdsByRoleId(roleId);
        return Result.success(menuIds);
    }

    @PostMapping("/assign")
    public Result<String> assignRoleMenus(@RequestBody AssignRoleMenuRequest request) {
        roleMenuService.assignRoleMenus(request.getRoleId(), request.getMenuIds());
        return Result.success("角色权限分配成功");
    }

    public static class AssignRoleMenuRequest {
        private Long roleId;
        private List<Long> menuIds;

        public Long getRoleId() {
            return roleId;
        }

        public void setRoleId(Long roleId) {
            this.roleId = roleId;
        }

        public List<Long> getMenuIds() {
            return menuIds;
        }

        public void setMenuIds(List<Long> menuIds) {
            this.menuIds = menuIds;
        }
    }
}
