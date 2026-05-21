package com.englishdatamanager.backend.service;

import com.englishdatamanager.backend.entity.Category;
import com.englishdatamanager.backend.entity.PlaybackRecord;
import com.englishdatamanager.backend.entity.UserDownloadRecord;
import com.englishdatamanager.backend.entity.Video;
import com.englishdatamanager.backend.entity.VideoAlbum;
import com.englishdatamanager.backend.entity.VideoAlbumItem;
import com.englishdatamanager.backend.entity.VideoMediaAsset;
import com.englishdatamanager.backend.entity.VideoTranscriptSegment;
import com.englishdatamanager.backend.exception.BusinessException;
import com.englishdatamanager.backend.security.UserContext;
import com.englishdatamanager.backend.vo.AppCourseCardVo;
import com.englishdatamanager.backend.vo.AppCourseCategoryVo;
import com.englishdatamanager.backend.vo.AppCourseDetailVo;
import com.englishdatamanager.backend.vo.AppLessonPlayerVo;
import com.englishdatamanager.backend.vo.AppLessonSentenceVo;
import com.englishdatamanager.backend.vo.AppLessonVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppCourseQueryService {

    private final VideoAlbumService videoAlbumService;
    private final VideoAlbumItemService videoAlbumItemService;
    private final VideoService videoService;
    private final CategoryService categoryService;
    private final UserCourseAccessService userCourseAccessService;
    private final UserFavoriteService userFavoriteService;
    private final UserDownloadRecordService userDownloadRecordService;
    private final PlaybackRecordService playbackRecordService;
    private final VideoMediaAssetService videoMediaAssetService;
    private final VideoTranscriptSegmentService videoTranscriptSegmentService;

    public List<AppCourseCardVo> recommendCourses() {
        return toCourseCards(videoAlbumService.lambdaQuery()
                .eq(VideoAlbum::getStatus, 1)
                .orderByDesc(VideoAlbum::getIsFeatured)
                .orderByAsc(VideoAlbum::getSortNo)
                .orderByDesc(VideoAlbum::getId)
                .last("limit 12")
                .list());
    }

    public List<AppCourseCategoryVo> dailyReadingCategories() {
        return categoryService.lambdaQuery()
                .eq(Category::getStatus, 1)
                .eq(Category::getCategoryType, "daily_reading")
                .orderByAsc(Category::getSortNo)
                .list()
                .stream()
                .map(this::toCategoryVo)
                .toList();
    }

    public List<AppCourseCardVo> dailyReadingCourses(Long categoryId) {
        return toCourseCards(videoAlbumService.lambdaQuery()
                .eq(VideoAlbum::getStatus, 1)
                .eq(categoryId != null, VideoAlbum::getCategoryId, categoryId)
                .orderByDesc(VideoAlbum::getIsFeatured)
                .orderByAsc(VideoAlbum::getSortNo)
                .orderByDesc(VideoAlbum::getId)
                .list());
    }

    public List<AppCourseCardVo> courseList(Long categoryId, Boolean isVip, Integer limit) {
        int resolvedLimit = limit == null || limit <= 0 ? 10 : Math.min(limit, 100);
        return toCourseCards(videoAlbumService.lambdaQuery()
                .eq(VideoAlbum::getStatus, 1)
                .eq(categoryId != null, VideoAlbum::getCategoryId, categoryId)
                .and(Boolean.TRUE.equals(isVip),
                        wrapper -> wrapper.eq(VideoAlbum::getAccessType, "vip").or().gt(VideoAlbum::getPrice, BigDecimal.ZERO))
                .and(Boolean.FALSE.equals(isVip),
                        wrapper -> wrapper.ne(VideoAlbum::getAccessType, "vip").or().isNull(VideoAlbum::getAccessType))
                .orderByDesc(VideoAlbum::getIsFeatured)
                .orderByAsc(VideoAlbum::getSortNo)
                .orderByDesc(VideoAlbum::getId)
                .last("limit " + resolvedLimit)
                .list());
    }

    public List<AppCourseCardVo> purchasedCourses() {
        Long userId = requireUserId();
        List<Long> courseIds = userCourseAccessService.lambdaQuery()
                .eq(com.englishdatamanager.backend.entity.UserCourseAccess::getUserId, userId)
                .eq(com.englishdatamanager.backend.entity.UserCourseAccess::getStatus, 1)
                .list()
                .stream()
                .map(com.englishdatamanager.backend.entity.UserCourseAccess::getCourseId)
                .filter(Objects::nonNull)
                .toList();
        if (courseIds.isEmpty()) {
            return List.of();
        }
        return toCourseCards(videoAlbumService.listByIds(courseIds));
    }

    public AppCourseDetailVo courseDetail(Long courseId) {
        VideoAlbum album = getCourse(courseId);
        Long userId = UserContext.getUserId();
        boolean canLearn = userCourseAccessService.hasAccess(userId, album);
        List<AppLessonVo> lessons = courseLessons(courseId);

        AppCourseDetailVo result = new AppCourseDetailVo();
        result.setCourse(toCourseCard(album));
        result.setPublisher(album.getPublisherName());
        result.setPurchased(userId != null && userCourseAccessService.existsActiveAccess(userId, courseId));
        result.setCanLearn(canLearn);
        result.setLessons(lessons);
        return result;
    }

    public List<AppLessonVo> courseLessons(Long courseId) {
        VideoAlbum album = getCourse(courseId);
        List<VideoAlbumItem> items = videoAlbumItemService.lambdaQuery()
                .eq(VideoAlbumItem::getAlbumId, courseId)
                .orderByAsc(VideoAlbumItem::getSortNo)
                .orderByAsc(VideoAlbumItem::getId)
                .list();
        List<Long> lessonIds = items.stream().map(VideoAlbumItem::getVideoId).filter(Objects::nonNull).toList();
        Map<Long, Video> videoMap = lessonIds.isEmpty()
                ? Map.of()
                : videoService.listByIds(lessonIds).stream().collect(Collectors.toMap(Video::getId, Function.identity()));
        Set<Long> favoriteIds = UserContext.getUserId() == null
                ? Set.of()
                : userFavoriteService.listTargetIds(UserContext.getUserId(), UserFavoriteService.TARGET_TYPE_VIDEO).stream().collect(Collectors.toSet());
        Set<Long> downloadedIds = UserContext.getUserId() == null
                ? Set.of()
                : userDownloadRecordService.lambdaQuery()
                .eq(UserDownloadRecord::getUserId, UserContext.getUserId())
                .eq(UserDownloadRecord::getStatus, "done")
                .list()
                .stream()
                .map(UserDownloadRecord::getVideoId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, PlaybackRecord> playbackMap = UserContext.getUserId() == null || lessonIds.isEmpty()
                ? Map.of()
                : playbackRecordService.lambdaQuery()
                .eq(PlaybackRecord::getUserId, UserContext.getUserId())
                .in(PlaybackRecord::getVideoId, lessonIds)
                .list()
                .stream()
                .collect(Collectors.toMap(PlaybackRecord::getVideoId, Function.identity()));

        return items.stream()
                .map(item -> toLessonVo(videoMap.get(item.getVideoId()), favoriteIds, downloadedIds, playbackMap))
                .filter(Objects::nonNull)
                .toList();
    }

    public AppLessonPlayerVo lessonPlayer(Long courseId, Long lessonId) {
        VideoAlbum album = getCourse(courseId);
        Long userId = requireUserId();
        if (!userCourseAccessService.hasAccess(userId, album)) {
            throw new BusinessException("当前课程未购买或无权限学习");
        }
        Video lesson = getCourseLesson(courseId, lessonId);
        List<VideoTranscriptSegment> segments = videoTranscriptSegmentService.lambdaQuery()
                .eq(VideoTranscriptSegment::getVideoId, lessonId)
                .orderByAsc(VideoTranscriptSegment::getSortNo)
                .orderByAsc(VideoTranscriptSegment::getSegmentNo)
                .list();
        VideoMediaAsset playAsset = videoMediaAssetService.lambdaQuery()
                .eq(VideoMediaAsset::getVideoId, lessonId)
                .eq(VideoMediaAsset::getAssetType, "video")
                .eq(VideoMediaAsset::getStatus, 1)
                .orderByDesc(VideoMediaAsset::getIsDefault)
                .last("limit 1")
                .one();
        PlaybackRecord playbackRecord = playbackRecordService.lambdaQuery()
                .eq(PlaybackRecord::getUserId, userId)
                .eq(PlaybackRecord::getVideoId, lessonId)
                .one();

        AppLessonPlayerVo result = new AppLessonPlayerVo();
        result.setLesson(toLessonVo(
                lesson,
                userFavoriteService.listTargetIds(userId, UserFavoriteService.TARGET_TYPE_VIDEO).stream().collect(Collectors.toSet()),
                userDownloadRecordService.lambdaQuery()
                        .eq(UserDownloadRecord::getUserId, userId)
                        .eq(UserDownloadRecord::getStatus, "done")
                        .list()
                        .stream()
                        .map(UserDownloadRecord::getVideoId)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet()),
                playbackRecord == null ? Map.of() : Map.of(lessonId, playbackRecord)
        ));
        result.setAudioUrl(playAsset == null ? lesson.getVideoUrl() : playAsset.getFileUrl());
        result.setProgressSeconds(playbackRecord == null ? 0 : playbackRecord.getProgressSeconds());
        result.setPlaybackRate(playbackRecord == null || playbackRecord.getPlaybackRate() == null ? 1.0 : playbackRecord.getPlaybackRate());
        result.setCanDownload(lesson.getAllowDownload() != null && lesson.getAllowDownload() == 1);
        result.setShowChinese(true);
        result.setTabs(List.of("原文", "精讲", "教材", "词汇", "收藏", "笔记"));
        result.setTranscript(segments.stream().map(this::toSentenceVo).toList());
        return result;
    }

    public AppLessonPlayerVo lessonPlayerByLesson(Long lessonId) {
        VideoAlbumItem albumItem = videoAlbumItemService.lambdaQuery()
                .eq(VideoAlbumItem::getVideoId, lessonId)
                .orderByDesc(VideoAlbumItem::getIsDefaultAlbum)
                .orderByAsc(VideoAlbumItem::getSortNo)
                .last("limit 1")
                .one();
        if (albumItem == null || albumItem.getAlbumId() == null) {
            throw new BusinessException("lesson album not found");
        }
        return lessonPlayer(albumItem.getAlbumId(), lessonId);
    }

    public List<AppLessonSentenceVo> lessonTranscript(Long lessonId) {
        Video lesson = videoService.getById(lessonId);
        if (lesson == null || lesson.getPublishStatus() == null || lesson.getPublishStatus() != 1) {
            throw new BusinessException("lesson not found");
        }
        return videoTranscriptSegmentService.lambdaQuery()
                .eq(VideoTranscriptSegment::getVideoId, lessonId)
                .orderByAsc(VideoTranscriptSegment::getSortNo)
                .orderByAsc(VideoTranscriptSegment::getSegmentNo)
                .list()
                .stream()
                .map(this::toSentenceVo)
                .toList();
    }

    private List<AppCourseCardVo> toCourseCards(List<VideoAlbum> albums) {
        return albums.stream().map(this::toCourseCard).toList();
    }

    private AppCourseCardVo toCourseCard(VideoAlbum album) {
        Long userId = UserContext.getUserId();
        AppCourseCardVo vo = new AppCourseCardVo();
        vo.setId(String.valueOf(album.getId()));
        vo.setTitle(album.getTitle());
        vo.setSubtitle(album.getSubtitle());
        vo.setDescription(album.getDescription());
        vo.setImageUrl(album.getCoverUrl());
        vo.setVocabularyCount(album.getWordCount());
        vo.setPlayCount(album.getViewCount() == null ? 0L : album.getViewCount());
        vo.setTags(splitTags(album.getTags()));
        vo.setIsVip(isVipCourse(album));
        vo.setThemeColor(album.getThemeColor());
        vo.setAuthor(album.getAuthorName());
        vo.setPrice(album.getPrice() == null ? BigDecimal.ZERO : album.getPrice());
        vo.setPurchased(userId != null && userCourseAccessService.hasAccess(userId, album));
        return vo;
    }

    private AppCourseCategoryVo toCategoryVo(Category category) {
        AppCourseCategoryVo vo = new AppCourseCategoryVo();
        vo.setId(String.valueOf(category.getId()));
        vo.setName(category.getName());
        vo.setCode(category.getCode());
        return vo;
    }

    private AppLessonVo toLessonVo(Video video,
                                   Set<Long> favoriteIds,
                                   Set<Long> downloadedIds,
                                   Map<Long, PlaybackRecord> playbackMap) {
        if (video == null) {
            return null;
        }
        AppLessonVo vo = new AppLessonVo();
        vo.setId(String.valueOf(video.getId()));
        vo.setTitle(video.getTitleCn());
        vo.setDuration(formatDuration(video.getDurationSeconds()));
        PlaybackRecord playbackRecord = playbackMap.get(video.getId());
        vo.setIsLearned(playbackRecord != null && playbackRecord.getFinished() != null && playbackRecord.getFinished() == 1);
        vo.setDownloaded(downloadedIds.contains(video.getId()));
        vo.setCollected(favoriteIds.contains(video.getId()));
        return vo;
    }

    private AppLessonSentenceVo toSentenceVo(VideoTranscriptSegment segment) {
        AppLessonSentenceVo vo = new AppLessonSentenceVo();
        vo.setId(String.valueOf(segment.getId()));
        vo.setText(segment.getOriginText());
        vo.setTranslation(segment.getTranslationText());
        vo.setStartTime(segment.getStartMs() == null ? 0D : segment.getStartMs() / 1000D);
        vo.setDuration(segment.getEndMs() == null || segment.getStartMs() == null
                ? 0D
                : (segment.getEndMs() - segment.getStartMs()) / 1000D);
        return vo;
    }

    private List<String> splitTags(String tags) {
        if (tags == null || tags.isBlank()) {
            return List.of();
        }
        return Arrays.stream(tags.split(","))
                .map(String::trim)
                .filter(item -> !item.isBlank())
                .toList();
    }

    private boolean isVipCourse(VideoAlbum album) {
        return "vip".equalsIgnoreCase(album.getAccessType())
                || (album.getPrice() != null && album.getPrice().signum() > 0);
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
            throw new BusinessException("请先登录");
        }
        return userId;
    }

    private VideoAlbum getCourse(Long courseId) {
        VideoAlbum album = videoAlbumService.getById(courseId);
        if (album == null || album.getStatus() == null || album.getStatus() != 1) {
            throw new BusinessException("课程不存在");
        }
        return album;
    }

    private Video getCourseLesson(Long courseId, Long lessonId) {
        boolean exists = videoAlbumItemService.lambdaQuery()
                .eq(VideoAlbumItem::getAlbumId, courseId)
                .eq(VideoAlbumItem::getVideoId, lessonId)
                .exists();
        if (!exists) {
            throw new BusinessException("章节不存在");
        }
        Video lesson = videoService.getById(lessonId);
        if (lesson == null || lesson.getPublishStatus() == null || lesson.getPublishStatus() != 1) {
            throw new BusinessException("章节不可用");
        }
        return lesson;
    }
}
