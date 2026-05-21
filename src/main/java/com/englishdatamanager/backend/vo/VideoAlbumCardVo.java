package com.englishdatamanager.backend.vo;

import com.englishdatamanager.backend.entity.VideoAlbum;
import lombok.Data;

@Data
public class VideoAlbumCardVo {

    private VideoAlbum album;
    private Boolean favorited;
}
