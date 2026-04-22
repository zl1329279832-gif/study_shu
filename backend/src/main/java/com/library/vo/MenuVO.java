package com.library.vo;

import lombok.Data;

import java.util.List;

@Data
public class MenuVO {

    private Long id;

    private String menuName;

    private Long parentId;

    private String path;

    private String component;

    private String icon;

    private Integer sortOrder;

    private Integer menuType;

    private List<MenuVO> children;

}
