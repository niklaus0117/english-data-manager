package com.englishdatamanager.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("video_content")
public class VideoContent extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long videoId;
    private String contentType;
    private String title;
    private String contentBody;
    private Integer sortNo;
}
