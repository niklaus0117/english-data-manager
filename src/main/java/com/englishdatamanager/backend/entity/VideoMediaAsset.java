package com.englishdatamanager.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("video_media_asset")
public class VideoMediaAsset extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long videoId;
    private String assetType;
    private String fileUrl;
    private String storageProvider;
    private String containerFormat;
    private String resolution;
    private Integer bitrateKbps;
    private Long sizeBytes;
    private Integer durationSeconds;
    private Integer isDefault;
    private Integer status;
}
