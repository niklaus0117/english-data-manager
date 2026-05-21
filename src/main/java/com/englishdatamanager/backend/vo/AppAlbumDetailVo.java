package com.englishdatamanager.backend.vo;

import com.englishdatamanager.backend.entity.PlaybackRecord;
import com.englishdatamanager.backend.entity.VideoAlbum;
import lombok.Data;

import java.util.List;

@Data
public class AppAlbumDetailVo {

    private VideoAlbum album;
    private Boolean favorited;
    private PlaybackRecord continuePlayback;
    private List<AlbumVideoItemVo> videos;
}
