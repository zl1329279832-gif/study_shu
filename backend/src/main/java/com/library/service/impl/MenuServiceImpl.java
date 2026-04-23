package com.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.entity.Menu;
import com.library.mapper.MenuMapper;
import com.library.service.MenuService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {

    @Override
    public List<Menu> getMenuTree() {
        LambdaQueryWrapper<Menu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Menu::getStatus, 1)
                .orderByAsc(Menu::getSortOrder);
        List<Menu> menus = list(queryWrapper);

        Map<Long, List<Menu>> parentMap = menus.stream()
                .collect(Collectors.groupingBy(Menu::getParentId));

        menus.forEach(menu -> {
            menu.setChildren(parentMap.getOrDefault(menu.getId(), new ArrayList<>()));
        });

        return menus.stream()
                .filter(menu -> menu.getParentId() == 0)
                .collect(Collectors.toList());
    }
}
