package com.englishdatamanager.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PlaybackRequest {

    @NotNull(message = "videoId is required")
    private Long videoId;

    @NotNull(message = "progressSeconds is required")
    private Integer progressSeconds;

    private Integer finished = 0;

    private Long albumId;

    private Integer lastSegmentNo = 0;

    private Double playbackRate = 1.0;

    private String deviceId;
}
