package com.englishdatamanager.backend.controller.app;

import com.englishdatamanager.backend.common.ApiResponse;
import com.englishdatamanager.backend.dto.FavoriteRequest;
import com.englishdatamanager.backend.dto.PlaybackRequest;
import com.englishdatamanager.backend.entity.AppUser;
import com.englishdatamanager.backend.entity.PermissionGroup;
import com.englishdatamanager.backend.entity.PlaybackRecord;
import com.englishdatamanager.backend.entity.Video;
import com.englishdatamanager.backend.entity.VideoAlbum;
import com.englishdatamanager.backend.exception.BusinessException;
import com.englishdatamanager.backend.security.UserContext;
import com.englishdatamanager.backend.service.AppUserService;
import com.englishdatamanager.backend.service.PermissionGroupService;
import com.englishdatamanager.backend.service.PlaybackRecordService;
import com.englishdatamanager.backend.service.UserDownloadRecordService;
import com.englishdatamanager.backend.service.UserFavoriteService;
import com.englishdatamanager.backend.service.VideoAlbumService;
import com.englishdatamanager.backend.service.VideoService;
import com.englishdatamanager.backend.vo.AppLessonVo;
import com.englishdatamanager.backend.vo.AppUserVo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/app/user")
@RequiredArgsConstructor
public class AppUserController {

    private final AppUserService appUserService;
    private final PermissionGroupService permissionGroupService;
    private final UserFavoriteService userFavoriteService;
    private final VideoService videoService;
    private final VideoAlbumService videoAlbumService;
    private final PlaybackRecordService playbackRecordService;
    private final UserDownloadRecordService userDownloadRecordService;

    @GetMapping("/profile")
    public ApiResponse<AppUser> profile() {
        return ApiResponse.success(appUserService.getById(UserContext.getUserId()));
    }

    @GetMapping("/profile-card")
    public ApiResponse<AppUserVo> profileCard() {
        AppUser user = appUserService.getById(UserContext.getUserId());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        AppUserVo vo = new AppUserVo();
        vo.setId(String.valueOf(user.getId()));
        vo.setPhoneNumber(maskMobile(user.getMobile()));
        vo.setNickname(user.getNickname());
        vo.setAvatar(user.getAvatarUrl());
        return ApiResponse.success(vo);
    }

    @GetMapping("/group")
    public ApiResponse<Map<String, Object>> group() {
        AppUser appUser = appUserService.getById(UserContext.getUserId());
        if (appUser == null || appUser.getStatus() == null || appUser.getStatus() != 1) {
            throw new BusinessException("当前用户状态不可查看分组信息");
        }
        PermissionGroup group = permissionGroupService.getById(appUser.getPermissionGroupId());
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("group", group);
        result.put("features", group == null || group.getFeatureCodes() == null || group.getFeatureCodes().isBlank()
                ? List.of()
                : Arrays.stream(group.getFeatureCodes().split(","))
                .map(String::trim)
                .filter(item -> !item.isBlank())
                .toList());
        return ApiResponse.success(result);
    }

    @GetMapping("/favorites")
    public ApiResponse<List<Video>> favorites() {
        List<Long> videoIds = userFavoriteService.listTargetIds(UserContext.getUserId(), UserFavoriteService.TARGET_TYPE_VIDEO);
        if (videoIds.isEmpty()) {
            return ApiResponse.success(List.of());
        }
        return ApiResponse.success(videoService.listByIds(videoIds));
    }

    @GetMapping("/favorite-albums")
    public ApiResponse<List<VideoAlbum>> favoriteAlbums() {
        List<Long> albumIds = userFavoriteService.listTargetIds(UserContext.getUserId(), UserFavoriteService.TARGET_TYPE_ALBUM);
        if (albumIds.isEmpty()) {
            return ApiResponse.success(List.of());
        }
        return ApiResponse.success(videoAlbumService.listByIds(albumIds));
    }

    @GetMapping("/bookshelf")
    public ApiResponse<List<AppLessonVo>> bookshelf() {
        List<Long> lessonIds = userFavoriteService.listTargetIds(UserContext.getUserId(), UserFavoriteService.TARGET_TYPE_VIDEO);
        if (lessonIds.isEmpty()) {
            return ApiResponse.success(List.of());
        }
        Map<Long, PlaybackRecord> playbackMap = playbackRecordService.lambdaQuery()
                .eq(PlaybackRecord::getUserId, UserContext.getUserId())
                .in(PlaybackRecord::getVideoId, lessonIds)
                .list()
                .stream()
                .collect(java.util.stream.Collectors.toMap(PlaybackRecord::getVideoId, java.util.function.Function.identity()));
        List<Long> downloadedIds = userDownloadRecordService.lambdaQuery()
                .eq(com.englishdatamanager.backend.entity.UserDownloadRecord::getUserId, UserContext.getUserId())
                .eq(com.englishdatamanager.backend.entity.UserDownloadRecord::getStatus, "done")
                .list()
                .stream()
                .map(com.englishdatamanager.backend.entity.UserDownloadRecord::getVideoId)
                .toList();
        return ApiResponse.success(videoService.listByIds(lessonIds).stream().map(video -> {
            AppLessonVo vo = new AppLessonVo();
            vo.setId(String.valueOf(video.getId()));
            vo.setTitle(video.getTitleCn());
            vo.setDuration(formatDuration(video.getDurationSeconds()));
            PlaybackRecord playback = playbackMap.get(video.getId());
            vo.setIsLearned(playback != null && playback.getFinished() != null && playback.getFinished() == 1);
            vo.setCollected(true);
            vo.setDownloaded(downloadedIds.contains(video.getId()));
            return vo;
        }).toList());
    }

    @GetMapping("/cache")
    public ApiResponse<List<AppLessonVo>> cacheList() {
        List<com.englishdatamanager.backend.entity.UserDownloadRecord> downloads = userDownloadRecordService.lambdaQuery()
                .eq(com.englishdatamanager.backend.entity.UserDownloadRecord::getUserId, UserContext.getUserId())
                .eq(com.englishdatamanager.backend.entity.UserDownloadRecord::getStatus, "done")
                .orderByDesc(com.englishdatamanager.backend.entity.UserDownloadRecord::getId)
                .list();
        List<Long> lessonIds = downloads.stream().map(com.englishdatamanager.backend.entity.UserDownloadRecord::getVideoId).toList();
        if (lessonIds.isEmpty()) {
            return ApiResponse.success(List.of());
        }
        Map<Long, Video> videoMap = videoService.listByIds(lessonIds).stream()
                .collect(java.util.stream.Collectors.toMap(Video::getId, java.util.function.Function.identity()));
        return ApiResponse.success(downloads.stream().map(item -> {
            Video video = videoMap.get(item.getVideoId());
            if (video == null) {
                return null;
            }
            AppLessonVo vo = new AppLessonVo();
            vo.setId(String.valueOf(video.getId()));
            vo.setTitle(video.getTitleCn());
            vo.setDuration(formatDuration(video.getDurationSeconds()));
            vo.setDownloaded(true);
            vo.setCollected(userFavoriteService.isFavorited(UserContext.getUserId(), UserFavoriteService.TARGET_TYPE_VIDEO, video.getId()));
            vo.setIsLearned(false);
            return vo;
        }).filter(java.util.Objects::nonNull).toList());
    }

    @PostMapping("/favorites")
    public ApiResponse<Void> addFavorite(@Valid @RequestBody FavoriteRequest request) {
        Long targetId = request.resolveTargetId();
        if (targetId == null) {
            throw new BusinessException("targetId is required");
        }
        userFavoriteService.addFavorite(UserContext.getUserId(), request.getTargetType(), targetId);
        return ApiResponse.success();
    }

    @DeleteMapping("/favorites")
    public ApiResponse<Void> deleteFavorite(@RequestParam String targetType, @RequestParam Long targetId) {
        userFavoriteService.removeFavorite(UserContext.getUserId(), targetType, targetId);
        return ApiResponse.success();
    }

    @DeleteMapping("/favorites/{videoId}")
    public ApiResponse<Void> deleteFavorite(@PathVariable Long videoId) {
        userFavoriteService.removeFavorite(UserContext.getUserId(), UserFavoriteService.TARGET_TYPE_VIDEO, videoId);
        return ApiResponse.success();
    }

    @GetMapping("/playbacks")
    public ApiResponse<List<PlaybackRecord>> playbacks() {
        return ApiResponse.success(playbackRecordService.lambdaQuery()
                .eq(PlaybackRecord::getUserId, UserContext.getUserId())
                .orderByDesc(PlaybackRecord::getLastPlayedAt)
                .last("limit 50")
                .list());
    }

    @PostMapping("/playbacks")
    public ApiResponse<Void> addPlayback(@Valid @RequestBody PlaybackRequest request) {
        playbackRecordService.saveOrUpdateRecord(
                UserContext.getUserId(),
                request.getVideoId(),
                request.getAlbumId(),
                request.getProgressSeconds(),
                request.getLastSegmentNo(),
                request.getPlaybackRate(),
                request.getFinished()
                ,
                request.getDeviceId()
        );
        return ApiResponse.success();
    }

    private String formatDuration(Integer durationSeconds) {
        if (durationSeconds == null || durationSeconds <= 0) {
            return null;
        }
        int minutes = durationSeconds / 60;
        int seconds = durationSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    private String maskMobile(String mobile) {
        if (mobile == null || mobile.length() < 7) {
            return mobile;
        }
        return mobile.substring(0, 3) + "****" + mobile.substring(mobile.length() - 4);
    }
}
