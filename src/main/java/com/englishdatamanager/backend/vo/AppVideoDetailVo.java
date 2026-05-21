package com.englishdatamanager.backend.vo;

import com.englishdatamanager.backend.entity.Video;
import com.englishdatamanager.backend.entity.VideoContent;
import com.englishdatamanager.backend.entity.VideoMediaAsset;
import com.englishdatamanager.backend.entity.VideoSubtitle;
import com.englishdatamanager.backend.entity.VideoSubtitleTrack;
import com.englishdatamanager.backend.entity.VideoTranscriptSegment;
import lombok.Data;

import java.util.List;

@Data
public class AppVideoDetailVo {

    private Video video;
    private VideoMediaAsset playAsset;
    private List<VideoMediaAsset> mediaAssets;
    private List<VideoSubtitleTrack> subtitleTracks;
    private List<VideoTranscriptSegment> transcriptSegments;
    private List<VideoSubtitle> subtitles;
    private List<VideoContent> contents;
    private Boolean translationLocked;
    private VideoUserStateVo userState;
}
