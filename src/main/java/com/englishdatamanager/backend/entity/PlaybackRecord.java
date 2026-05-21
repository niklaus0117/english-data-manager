package com.englishdatamanager.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("playback_record")
public class PlaybackRecord extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long videoId;
    private Long albumId;
    private Long mediaAssetId;
    private Integer progressSeconds;
    private Integer lastSegmentNo;
    private Double playbackRate;
    private Integer finished;
    private String deviceId;
    private LocalDateTime lastPlayedAt;
}
