package com.englishdatamanager.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("video_transcript_segment")
public class VideoTranscriptSegment extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long videoId;
    private Long subtitleTrackId;
    private String trackGroup;
    private Integer segmentNo;
    private Integer startMs;
    private Integer endMs;
    private String speakerName;
    private String speakerRole;
    private String originText;
    private String translationText;
    private String noteText;
    private String keywordsJson;
    private Integer isTranslationLocked;
    private Integer sortNo;
}
