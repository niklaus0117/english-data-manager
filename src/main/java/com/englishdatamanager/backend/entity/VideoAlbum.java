package com.englishdatamanager.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("video_album")
public class VideoAlbum extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long categoryId;
    private String albumType;
    private String title;
    private String subtitle;
    private String sourceName;
    private String sourcePlatform;
    private String coverUrl;
    private String bannerUrl;
    private String description;
    private String tags;
    private String authorName;
    private String publisherName;
    private BigDecimal price;
    private String accessType;
    private String themeColor;
    private LocalDate publishDate;
    private Long viewCount;
    private Long favoriteCount;
    private Long shareCount;
    private Integer wordCount;
    private Integer isFeatured;
    private Integer status;
    private Integer sortNo;
}
