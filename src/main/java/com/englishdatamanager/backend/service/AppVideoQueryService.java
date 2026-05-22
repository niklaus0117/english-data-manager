package com.englishdatamanager.backend.service;

import com.englishdatamanager.backend.entity.AppUser;
import com.englishdatamanager.backend.entity.PlaybackRecord;
import com.englishdatamanager.backend.entity.Video;
import com.englishdatamanager.backend.entity.VideoContent;
import com.englishdatamanager.backend.entity.VideoMediaAsset;
import com.englishdatamanager.backend.entity.VideoSubtitle;
import com.englishdatamanager.backend.entity.VideoSubtitleTrack;
import com.englishdatamanager.backend.entity.VideoTranscriptSegment;
import com.englishdatamanager.backend.exception.BusinessException;
import com.englishdatamanager.backend.security.UserContext;
import com.englishdatamanager.backend.vo.AppVideoDetailVo;
import com.englishdatamanager.backend.vo.VideoUserStateVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppVideoQueryService {

    private final VideoService videoService;
    private final VideoSubtitleService videoSubtitleService;
    private final VideoContentService videoContentService;
    private final VideoMediaAssetService videoMediaAssetService;
    private final VideoSubtitleTrackService videoSubtitleTrackService;
    private final VideoTranscriptSegmentService videoTranscriptSegmentService;
    private final UserFavoriteService userFavoriteService;
    private final PlaybackRecordService playbackRecordService;
    private final AppUserService appUserService;

    /**
     * 查询 App 视频详情。
     */
    public AppVideoDetailVo getVideoDetail(Long videoId) {
        Video video = videoService.getById(videoId);
        if (video == null) {
            throw new BusinessException("video not found");
        }

        Long userId = UserContext.getUserId();
        AppUser appUser = userId == null ? null : appUserService.getById(userId);
        // 视频详情对游客可见，用户态字段按登录状态补充，未登录时返回默认状态。
        PlaybackRecord playbackRecord = userId == null ? null : playbackRecordService.lambdaQuery()
                .eq(PlaybackRecord::getUserId, userId)
                .eq(PlaybackRecord::getVideoId, videoId)
                .one();
        boolean favorited = userFavoriteService.isFavorited(userId, UserFavoriteService.TARGET_TYPE_VIDEO, videoId);

        List<VideoSubtitle> subtitles = videoSubtitleService.lambdaQuery()
                .eq(VideoSubtitle::getVideoId, videoId)
                .list();
        List<VideoSubtitleTrack> subtitleTracks = videoSubtitleTrackService.lambdaQuery()
                .eq(VideoSubtitleTrack::getVideoId, videoId)
                .orderByDesc(VideoSubtitleTrack::getIsDefault)
                .orderByAsc(VideoSubtitleTrack::getId)
                .list();
        List<VideoContent> contents = videoContentService.lambdaQuery()
                .eq(VideoContent::getVideoId, videoId)
                .orderByAsc(VideoContent::getSortNo)
                .list();
        List<VideoMediaAsset> mediaAssets = videoMediaAssetService.lambdaQuery()
                .eq(VideoMediaAsset::getVideoId, videoId)
                .eq(VideoMediaAsset::getStatus, 1)
                .orderByDesc(VideoMediaAsset::getIsDefault)
                .orderByAsc(VideoMediaAsset::getId)
                .list();
        List<VideoTranscriptSegment> transcriptSegments = videoTranscriptSegmentService.lambdaQuery()
                .eq(VideoTranscriptSegment::getVideoId, videoId)
                .orderByAsc(VideoTranscriptSegment::getSortNo)
                .orderByAsc(VideoTranscriptSegment::getSegmentNo)
                .list();

        boolean vipActive = isVipActive(appUser);
        // 任一脚本片段锁定翻译时，非 VIP 用户不可查看中文翻译。
        boolean translationLocked = transcriptSegments.stream()
                .anyMatch(segment -> segment.getIsTranslationLocked() != null && segment.getIsTranslationLocked() == 1)
                && !vipActive;

        VideoUserStateVo userState = new VideoUserStateVo();
        userState.setFavorited(favorited);
        userState.setProgressSeconds(playbackRecord == null ? 0 : playbackRecord.getProgressSeconds());
        userState.setFinished(playbackRecord != null && playbackRecord.getFinished() != null
                && playbackRecord.getFinished() == 1);
        userState.setCanViewTranslation(!translationLocked);
        userState.setCanDownload(resolveCanDownload(video, vipActive));
        userState.setPlaybackRate(playbackRecord == null || playbackRecord.getPlaybackRate() == null
                ? 1.0
                : playbackRecord.getPlaybackRate());

        AppVideoDetailVo result = new AppVideoDetailVo();
        result.setVideo(video);
        result.setPlayAsset(mediaAssets.stream()
                // 默认媒体资源已经在查询中排在前面，这里取第一个 video 类型资源作为播放源。
                .filter(asset -> "video".equalsIgnoreCase(asset.getAssetType()))
                .findFirst()
                .orElse(null));
        result.setMediaAssets(mediaAssets);
        result.setSubtitleTracks(subtitleTracks);
        result.setTranscriptSegments(transcriptSegments);
        result.setSubtitles(subtitles);
        result.setContents(contents);
        result.setTranslationLocked(translationLocked);
        result.setUserState(userState);
        return result;
    }

    /**
     * 判断用户 VIP 是否仍在有效期内。
     */
    private boolean isVipActive(AppUser appUser) {
        return appUser != null
                && appUser.getVipExpireAt() != null
                && appUser.getVipExpireAt().isAfter(LocalDateTime.now());
    }

    /**
     * 根据视频配置和会员状态判断是否可下载。
     */
    private boolean resolveCanDownload(Video video, boolean vipActive) {
        return video != null
                && video.getAllowDownload() != null
                && video.getAllowDownload() == 1
                && vipActive;
    }
}
