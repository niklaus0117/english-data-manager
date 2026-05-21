package com.englishdatamanager.backend.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class AppCourseCardVo {

    private String id;
    private String title;
    private String subtitle;
    private String description;
    private String imageUrl;
    private Integer vocabularyCount;
    private Long playCount;
    private List<String> tags;
    private Boolean isVip;
    private String themeColor;
    private String author;
    private BigDecimal price;
    private Boolean purchased;
}
