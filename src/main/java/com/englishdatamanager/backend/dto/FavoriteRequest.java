package com.englishdatamanager.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FavoriteRequest {

    private String targetType = "video";

    private Long targetId;

    private Long videoId;

    /**
     * 解析收藏请求中的目标 ID。
     */
    public Long resolveTargetId() {
        return targetId != null ? targetId : videoId;
    }
}
