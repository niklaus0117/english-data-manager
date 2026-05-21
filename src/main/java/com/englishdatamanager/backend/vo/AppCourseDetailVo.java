package com.englishdatamanager.backend.vo;

import lombok.Data;

import java.util.List;

@Data
public class AppCourseDetailVo {

    private AppCourseCardVo course;
    private String publisher;
    private Boolean purchased;
    private Boolean canLearn;
    private List<AppLessonVo> lessons;
}
