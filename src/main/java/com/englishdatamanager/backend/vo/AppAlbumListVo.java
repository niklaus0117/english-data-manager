package com.englishdatamanager.backend.vo;

import com.englishdatamanager.backend.entity.Category;
import lombok.Data;

import java.util.List;

@Data
public class AppAlbumListVo {

    private Category category;
    private List<VideoAlbumCardVo> albums;
}
