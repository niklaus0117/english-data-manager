package com.englishdatamanager.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FavoriteRequest {

    private String targetType = "video";

    private Long targetId;

    private Long videoId;

    public Long resolveTargetId() {
        return targetId != null ? targetId : videoId;
    }
}
