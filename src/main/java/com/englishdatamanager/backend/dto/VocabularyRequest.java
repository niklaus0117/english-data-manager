package com.englishdatamanager.backend.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VocabularyRequest {

    @NotBlank(message = "word is required")
    private String word;

    private String phonetic;

    private String translation;

    @JsonAlias({"lessonId", "lesson_id", "videoId", "video_id"})
    private Long sourceVideoId;
}
