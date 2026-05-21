package com.englishdatamanager.backend.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AdminMenuTreeVo {

    private Long id;
    private Long parentId;
    private String menuName;
    private String menuCode;
    private String path;
    private String component;
    private String icon;
    private Integer menuType;
    private Integer sortNo;
    private List<AdminMenuTreeVo> children = new ArrayList<>();
}
