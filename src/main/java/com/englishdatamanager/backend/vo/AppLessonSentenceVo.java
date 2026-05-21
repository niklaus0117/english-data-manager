package com.englishdatamanager.backend.vo;

import lombok.Data;

@Data
public class AppLessonSentenceVo {

    private String id;
    private String text;
    private String translation;
    private Double startTime;
    private Double duration;
}
