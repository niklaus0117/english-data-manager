package com.englishdatamanager.backend.vo;

import com.englishdatamanager.backend.entity.Video;
import lombok.Data;

@Data
public class AlbumVideoItemVo {

    private Long albumItemId;
    private Integer sortNo;
    private Video video;
    private Boolean translationReady;
    private Boolean favorited;
}
