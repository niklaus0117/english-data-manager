package com.englishdatamanager.backend.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserNoteRequest {

    private Long id;

    @JsonAlias({"lessonId", "lesson_id", "video_id"})
    private Long videoId;

    @JsonAlias({"videoTimestamp", "video_timestamp", "timestamp"})
    private Integer videoTimestamp = 0;

    @NotBlank(message = "content is required")
    private String content;
}
