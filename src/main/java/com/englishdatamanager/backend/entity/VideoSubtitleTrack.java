package com.englishdatamanager.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("video_subtitle_track")
public class VideoSubtitleTrack extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long videoId;
    private String trackType;
    private String languageCode;
    private String fileFormat;
    private String subtitleUrl;
    private Integer isDefault;
    private Integer isAiGenerated;
    private Integer status;
}
