package com.englishdatamanager.backend.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.englishdatamanager.backend.common.ApiResponse;
import com.englishdatamanager.backend.common.PageResponse;
import com.englishdatamanager.backend.entity.Video;
import com.englishdatamanager.backend.entity.VideoContent;
import com.englishdatamanager.backend.entity.VideoMediaAsset;
import com.englishdatamanager.backend.entity.VideoSubtitle;
import com.englishdatamanager.backend.entity.VideoSubtitleTrack;
import com.englishdatamanager.backend.entity.VideoTranscriptSegment;
import com.englishdatamanager.backend.service.VideoContentService;
import com.englishdatamanager.backend.service.VideoMediaAssetService;
import com.englishdatamanager.backend.service.VideoService;
import com.englishdatamanager.backend.service.VideoSubtitleService;
import com.englishdatamanager.backend.service.VideoSubtitleTrackService;
import com.englishdatamanager.backend.service.VideoTranscriptSegmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/videos")
@RequiredArgsConstructor
public class AdminVideoController {

    private final VideoService videoService;
    private final VideoSubtitleService videoSubtitleService;
    private final VideoContentService videoContentService;
    private final VideoMediaAssetService videoMediaAssetService;
    private final VideoSubtitleTrackService videoSubtitleTrackService;
    private final VideoTranscriptSegmentService videoTranscriptSegmentService;

    /**
     * 分页查询数据列表。
     */
    @GetMapping
    public ApiResponse<PageResponse<Video>> page(
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Integer publishStatus,
            @RequestParam(required = false) String keyword) {
        Page<Video> page = videoService.lambdaQuery()
                .eq(categoryId != null, Video::getCategoryId, categoryId)
                .eq(publishStatus != null, Video::getPublishStatus, publishStatus)
                .and(keyword != null && !keyword.isBlank(),
                        wrapper -> wrapper.like(Video::getTitleCn, keyword).or().like(Video::getTitleEn, keyword))
                .orderByDesc(Video::getId)
                .page(new Page<>(current, size));
        return ApiResponse.success(PageResponse.of(page));
    }

    /**
     * 查询数据详情。
     */
    @GetMapping("/{id}")
    public ApiResponse<Video> detail(@PathVariable Long id) {
        return ApiResponse.success(videoService.getById(id));
    }

    /**
     * 创建一条业务数据。
     */
    @PostMapping
    public ApiResponse<Void> create(@RequestBody Video video) {
        videoService.createVideo(video);
        return ApiResponse.success();
    }

    /**
     * 更新指定业务数据。
     */
    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id, @RequestBody Video video) {
        video.setId(id);
        videoService.updateVideo(video);
        return ApiResponse.success();
    }

    /**
     * 删除指定业务数据。
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        videoService.removeById(id);
        return ApiResponse.success();
    }

    /**
     * 查询视频字幕列表。
     */
    @GetMapping("/{videoId}/subtitles")
    public ApiResponse<List<VideoSubtitle>> subtitleList(@PathVariable Long videoId) {
        return ApiResponse.success(videoSubtitleService.lambdaQuery()
                .eq(VideoSubtitle::getVideoId, videoId)
                .list());
    }

    /**
     * 创建视频字幕。
     */
    @PostMapping("/{videoId}/subtitles")
    public ApiResponse<Void> createSubtitle(@PathVariable Long videoId, @RequestBody VideoSubtitle subtitle) {
        subtitle.setVideoId(videoId);
        videoSubtitleService.save(subtitle);
        return ApiResponse.success();
    }

    /**
     * 更新视频字幕。
     */
    @PutMapping("/subtitles/{id}")
    public ApiResponse<Void> updateSubtitle(@PathVariable Long id, @RequestBody VideoSubtitle subtitle) {
        subtitle.setId(id);
        videoSubtitleService.updateById(subtitle);
        return ApiResponse.success();
    }

    /**
     * 删除视频字幕。
     */
    @DeleteMapping("/subtitles/{id}")
    public ApiResponse<Void> deleteSubtitle(@PathVariable Long id) {
        videoSubtitleService.removeById(id);
        return ApiResponse.success();
    }

    /**
     * 查询视频扩展内容列表。
     */
    @GetMapping("/{videoId}/contents")
    public ApiResponse<List<VideoContent>> contentList(@PathVariable Long videoId) {
        return ApiResponse.success(videoContentService.lambdaQuery()
                .eq(VideoContent::getVideoId, videoId)
                .orderByAsc(VideoContent::getSortNo)
                .list());
    }

    /**
     * 创建视频扩展内容。
     */
    @PostMapping("/{videoId}/contents")
    public ApiResponse<Void> createContent(@PathVariable Long videoId, @RequestBody VideoContent content) {
        content.setVideoId(videoId);
        videoContentService.save(content);
        return ApiResponse.success();
    }

    /**
     * 更新视频扩展内容。
     */
    @PutMapping("/contents/{id}")
    public ApiResponse<Void> updateContent(@PathVariable Long id, @RequestBody VideoContent content) {
        content.setId(id);
        videoContentService.updateById(content);
        return ApiResponse.success();
    }

    /**
     * 删除视频扩展内容。
     */
    @DeleteMapping("/contents/{id}")
    public ApiResponse<Void> deleteContent(@PathVariable Long id) {
        videoContentService.removeById(id);
        return ApiResponse.success();
    }

    /**
     * 查询视频媒体资源列表。
     */
    @GetMapping("/{videoId}/media-assets")
    public ApiResponse<List<VideoMediaAsset>> mediaAssetList(@PathVariable Long videoId) {
        return ApiResponse.success(videoMediaAssetService.lambdaQuery()
                .eq(VideoMediaAsset::getVideoId, videoId)
                .orderByDesc(VideoMediaAsset::getIsDefault)
                .orderByAsc(VideoMediaAsset::getId)
                .list());
    }

    /**
     * 创建视频媒体资源。
     */
    @PostMapping("/{videoId}/media-assets")
    public ApiResponse<Void> createMediaAsset(@PathVariable Long videoId, @RequestBody VideoMediaAsset asset) {
        asset.setVideoId(videoId);
        videoMediaAssetService.save(asset);
        return ApiResponse.success();
    }

    /**
     * 更新视频媒体资源。
     */
    @PutMapping("/media-assets/{id}")
    public ApiResponse<Void> updateMediaAsset(@PathVariable Long id, @RequestBody VideoMediaAsset asset) {
        asset.setId(id);
        videoMediaAssetService.updateById(asset);
        return ApiResponse.success();
    }

    /**
     * 删除视频媒体资源。
     */
    @DeleteMapping("/media-assets/{id}")
    public ApiResponse<Void> deleteMediaAsset(@PathVariable Long id) {
        videoMediaAssetService.removeById(id);
        return ApiResponse.success();
    }

    /**
     * 查询视频字幕轨道列表。
     */
    @GetMapping("/{videoId}/subtitle-tracks")
    public ApiResponse<List<VideoSubtitleTrack>> subtitleTrackList(@PathVariable Long videoId) {
        return ApiResponse.success(videoSubtitleTrackService.lambdaQuery()
                .eq(VideoSubtitleTrack::getVideoId, videoId)
                .orderByDesc(VideoSubtitleTrack::getIsDefault)
                .orderByAsc(VideoSubtitleTrack::getId)
                .list());
    }

    /**
     * 创建视频字幕轨道。
     */
    @PostMapping("/{videoId}/subtitle-tracks")
    public ApiResponse<Void> createSubtitleTrack(@PathVariable Long videoId, @RequestBody VideoSubtitleTrack track) {
        track.setVideoId(videoId);
        videoSubtitleTrackService.save(track);
        return ApiResponse.success();
    }

    /**
     * 更新视频字幕轨道。
     */
    @PutMapping("/subtitle-tracks/{id}")
    public ApiResponse<Void> updateSubtitleTrack(@PathVariable Long id, @RequestBody VideoSubtitleTrack track) {
        track.setId(id);
        videoSubtitleTrackService.updateById(track);
        return ApiResponse.success();
    }

    /**
     * 删除视频字幕轨道。
     */
    @DeleteMapping("/subtitle-tracks/{id}")
    public ApiResponse<Void> deleteSubtitleTrack(@PathVariable Long id) {
        videoSubtitleTrackService.removeById(id);
        return ApiResponse.success();
    }

    /**
     * 查询视频逐句脚本列表。
     */
    @GetMapping("/{videoId}/transcript-segments")
    public ApiResponse<List<VideoTranscriptSegment>> transcriptSegmentList(@PathVariable Long videoId) {
        return ApiResponse.success(videoTranscriptSegmentService.lambdaQuery()
                .eq(VideoTranscriptSegment::getVideoId, videoId)
                .orderByAsc(VideoTranscriptSegment::getSortNo)
                .orderByAsc(VideoTranscriptSegment::getSegmentNo)
                .list());
    }

    /**
     * 创建视频逐句脚本。
     */
    @PostMapping("/{videoId}/transcript-segments")
    public ApiResponse<Void> createTranscriptSegment(@PathVariable Long videoId,
                                                     @RequestBody VideoTranscriptSegment segment) {
        segment.setVideoId(videoId);
        videoTranscriptSegmentService.save(segment);
        return ApiResponse.success();
    }

    /**
     * 更新视频逐句脚本。
     */
    @PutMapping("/transcript-segments/{id}")
    public ApiResponse<Void> updateTranscriptSegment(@PathVariable Long id,
                                                     @RequestBody VideoTranscriptSegment segment) {
        segment.setId(id);
        videoTranscriptSegmentService.updateById(segment);
        return ApiResponse.success();
    }

    /**
     * 删除视频逐句脚本。
     */
    @DeleteMapping("/transcript-segments/{id}")
    public ApiResponse<Void> deleteTranscriptSegment(@PathVariable Long id) {
        videoTranscriptSegmentService.removeById(id);
        return ApiResponse.success();
    }
}
