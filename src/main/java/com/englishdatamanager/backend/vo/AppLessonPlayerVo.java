package com.englishdatamanager.backend.vo;

import lombok.Data;

import java.util.List;

@Data
public class AppLessonPlayerVo {

    private AppLessonVo lesson;
    private String audioUrl;
    private Integer progressSeconds;
    private Double playbackRate;
    private Boolean canDownload;
    private Boolean showChinese;
    private List<String> tabs;
    private List<AppLessonSentenceVo> transcript;
}
