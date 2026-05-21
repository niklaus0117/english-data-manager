package com.englishdatamanager.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("video")
public class Video extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long categoryId;
    private String titleCn;
    private String titleEn;
    private String titleOrigin;
    private String shortTitle;
    private String coverUrl;
    private String posterUrl;
    private String videoUrl;
    private Integer difficultyLevel;
    private Integer durationSeconds;
    private String sourceType;
    private String sourcePlatform;
    private String sourceUrl;
    private String sourceVideoCode;
    private Long fileSizeBytes;
    private String speaker;
    private String speakerSummary;
    private String tags;
    private String summary;
    private LocalDateTime publishedAt;
    private Long viewCount;
    private Long favoriteCount;
    private Long shareCount;
    private Integer wordCount;
    private String subtitleStatus;
    private String translationStatus;
    private String accessLevel;
    private String translationAccessLevel;
    private Integer allowDownload;
    private Integer trialSeconds;
    private Integer publishStatus;
    private Integer reviewStatus;
    private Integer recommendationStatus;
    private Integer sortNo;
}
