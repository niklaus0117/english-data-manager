package com.englishdatamanager.backend.controller.client;

import com.englishdatamanager.backend.common.ApiResponse;
import com.englishdatamanager.backend.dto.FavoriteRequest;
import com.englishdatamanager.backend.dto.UserNoteRequest;
import com.englishdatamanager.backend.dto.UserProgressRequest;
import com.englishdatamanager.backend.dto.VocabularyRequest;
import com.englishdatamanager.backend.entity.PlaybackRecord;
import com.englishdatamanager.backend.entity.UserNote;
import com.englishdatamanager.backend.entity.UserVocabulary;
import com.englishdatamanager.backend.entity.Video;
import com.englishdatamanager.backend.exception.BusinessException;
import com.englishdatamanager.backend.security.UserContext;
import com.englishdatamanager.backend.service.AppUserService;
import com.englishdatamanager.backend.service.PlaybackRecordService;
import com.englishdatamanager.backend.service.UserFavoriteService;
import com.englishdatamanager.backend.service.UserNoteService;
import com.englishdatamanager.backend.service.UserVocabularyService;
import com.englishdatamanager.backend.service.VideoService;
import com.englishdatamanager.backend.vo.AppLessonVo;
import com.englishdatamanager.backend.vo.AppUserVo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class ClientUserController {

    private final AppUserService appUserService;
    private final PlaybackRecordService playbackRecordService;
    private final UserFavoriteService userFavoriteService;
    private final UserNoteService userNoteService;
    private final UserVocabularyService userVocabularyService;
    private final VideoService videoService;

    @GetMapping("/profile")
    public ApiResponse<AppUserVo> profile() {
        return ApiResponse.success(appUserService.currentUserCard());
    }

    @GetMapping("/group")
    public ApiResponse<Object> group() {
        return ApiResponse.success(appUserService.currentUserGroup());
    }

    @PostMapping("/progress")
    public ApiResponse<Void> progress(@Valid @RequestBody UserProgressRequest request) {
        playbackRecordService.saveOrUpdateRecord(
                requireUserId(),
                request.getVideoId(),
                request.getAlbumId(),
                request.getProgressSeconds(),
                request.getLastSegmentNo(),
                request.getPlaybackRate(),
                request.resolveFinished(),
                request.getDeviceId()
        );
        return ApiResponse.success();
    }

    @GetMapping("/playbacks")
    public ApiResponse<List<PlaybackRecord>> playbacks() {
        return ApiResponse.success(playbackRecordService.lambdaQuery()
                .eq(PlaybackRecord::getUserId, requireUserId())
                .orderByDesc(PlaybackRecord::getLastPlayedAt)
                .last("limit 50")
                .list());
    }

    @GetMapping("/notes")
    public ApiResponse<List<UserNote>> notes(@RequestParam(required = false) Long lessonId,
                                             @RequestParam(required = false) Long videoId) {
        Long resolvedVideoId = videoId == null ? lessonId : videoId;
        return ApiResponse.success(userNoteService.lambdaQuery()
                .eq(UserNote::getUserId, requireUserId())
                .eq(resolvedVideoId != null, UserNote::getVideoId, resolvedVideoId)
                .orderByDesc(UserNote::getUpdateTime)
                .list());
    }

    @PostMapping("/notes")
    public ApiResponse<UserNote> saveNote(@Valid @RequestBody UserNoteRequest request) {
        if (request.getVideoId() == null) {
            throw new BusinessException("lessonId is required");
        }
        UserNote note = request.getId() == null ? null : userNoteService.getById(request.getId());
        if (note == null) {
            note = new UserNote();
            note.setUserId(requireUserId());
            note.setVideoId(request.getVideoId());
        } else if (!requireUserId().equals(note.getUserId())) {
            throw new BusinessException("note not found");
        }
        note.setVideoTimestamp(request.getVideoTimestamp());
        note.setContent(request.getContent());
        userNoteService.saveOrUpdate(note);
        return ApiResponse.success(note);
    }

    @PostMapping("/vocabulary")
    public ApiResponse<UserVocabulary> vocabulary(@Valid @RequestBody VocabularyRequest request) {
        String word = request.getWord().trim();
        UserVocabulary vocabulary = userVocabularyService.lambdaQuery()
                .eq(UserVocabulary::getUserId, requireUserId())
                .eq(UserVocabulary::getWord, word)
                .one();
        if (vocabulary == null) {
            vocabulary = new UserVocabulary();
            vocabulary.setUserId(requireUserId());
            vocabulary.setWord(word);
            vocabulary.setReviewCount(0);
        }
        vocabulary.setSourceVideoId(request.getSourceVideoId());
        vocabulary.setPhonetic(request.getPhonetic());
        vocabulary.setTranslation(request.getTranslation());
        userVocabularyService.saveOrUpdate(vocabulary);
        return ApiResponse.success(vocabulary);
    }

    @GetMapping("/vocabulary")
    public ApiResponse<List<UserVocabulary>> vocabularyList() {
        return ApiResponse.success(userVocabularyService.lambdaQuery()
                .eq(UserVocabulary::getUserId, requireUserId())
                .orderByDesc(UserVocabulary::getUpdateTime)
                .list());
    }

    @GetMapping("/collections")
    public ApiResponse<List<AppLessonVo>> collections() {
        List<Long> videoIds = userFavoriteService.listTargetIds(requireUserId(), UserFavoriteService.TARGET_TYPE_VIDEO);
        if (videoIds.isEmpty()) {
            return ApiResponse.success(List.of());
        }
        return ApiResponse.success(videoService.listByIds(videoIds).stream().map(this::toLessonVo).toList());
    }

    @PostMapping("/collections")
    public ApiResponse<Void> addCollection(@Valid @RequestBody FavoriteRequest request) {
        Long targetId = request.resolveTargetId();
        if (targetId == null) {
            throw new BusinessException("targetId is required");
        }
        userFavoriteService.addFavorite(requireUserId(), request.getTargetType(), targetId);
        return ApiResponse.success();
    }

    @DeleteMapping("/collections")
    public ApiResponse<Void> deleteCollection(@RequestParam(defaultValue = "video") String targetType,
                                              @RequestParam Long targetId) {
        userFavoriteService.removeFavorite(requireUserId(), targetType, targetId);
        return ApiResponse.success();
    }

    private AppLessonVo toLessonVo(Video video) {
        AppLessonVo vo = new AppLessonVo();
        vo.setId(String.valueOf(video.getId()));
        vo.setTitle(video.getTitleCn());
        vo.setDuration(formatDuration(video.getDurationSeconds()));
        vo.setCollected(true);
        vo.setDownloaded(false);
        vo.setIsLearned(false);
        return vo;
    }

    private String formatDuration(Integer durationSeconds) {
        if (durationSeconds == null || durationSeconds <= 0) {
            return null;
        }
        int minutes = durationSeconds / 60;
        int seconds = durationSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    private Long requireUserId() {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException("please login first");
        }
        return userId;
    }
}
