package com.englishdatamanager.backend.vo;

import lombok.Data;

@Data
public class AppLessonVo {

    private String id;
    private String title;
    private String duration;
    private Boolean isLearned;
    private Boolean downloaded;
    private Boolean collected;
}
