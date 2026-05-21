package com.englishdatamanager.backend.vo;

import lombok.Data;

@Data
public class VideoUserStateVo {

    private Boolean favorited;
    private Integer progressSeconds;
    private Boolean finished;
    private Boolean canViewTranslation;
    private Boolean canDownload;
    private Double playbackRate;
}
