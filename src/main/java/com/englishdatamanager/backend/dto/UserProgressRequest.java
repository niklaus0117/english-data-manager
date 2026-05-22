package com.englishdatamanager.backend.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserProgressRequest {

    @JsonAlias({"lessonId", "lesson_id", "video_id"})
    @NotNull(message = "lessonId is required")
    private Long videoId;

    private Long albumId;

    @JsonAlias({"progress_seconds"})
    @NotNull(message = "progressSeconds is required")
    private Integer progressSeconds;

    @JsonAlias({"last_segment_no"})
    private Integer lastSegmentNo = 0;

    @JsonAlias({"playback_rate"})
    private Double playbackRate = 1.0;

    @JsonAlias({"isLearned", "is_learned"})
    private Boolean learned = false;

    private Integer finished;

    private String deviceId;

    /**
     * 解析学习进度是否完成。
     */
    public Integer resolveFinished() {
        if (finished != null) {
            return finished;
        }
        return Boolean.TRUE.equals(learned) ? 1 : 0;
    }
}
