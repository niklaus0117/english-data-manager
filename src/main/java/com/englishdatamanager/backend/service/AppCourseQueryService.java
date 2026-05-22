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

    /**
     * 查询 App 首页推荐课程。
     */
    public List<AppCourseCardVo> recommendCourses() {
        return toCourseCards(videoAlbumService.lambdaQuery()
                .eq(VideoAlbum::getStatus, 1)
                .orderByDesc(VideoAlbum::getIsFeatured)
                .orderByAsc(VideoAlbum::getSortNo)
                .orderByDesc(VideoAlbum::getId)
                .last("limit 12")
                .list());
    }

    /**
     * 查询每日听读分类列表。
     */
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

    /**
     * 按分类查询每日听读课程。
     */
    public List<AppCourseCardVo> dailyReadingCourses(Long categoryId) {
        return toCourseCards(videoAlbumService.lambdaQuery()
                .eq(VideoAlbum::getStatus, 1)
                .eq(categoryId != null, VideoAlbum::getCategoryId, categoryId)
                .orderByDesc(VideoAlbum::getIsFeatured)
                .orderByAsc(VideoAlbum::getSortNo)
                .orderByDesc(VideoAlbum::getId)
                .list());
    }

    /**
     * 按条件查询 App 课程列表。
     */
    public List<AppCourseCardVo> courseList(Long categoryId, Boolean isVip, Integer limit) {
        int resolvedLimit = limit == null || limit <= 0 ? 10 : Math.min(limit, 100);
        return toCourseCards(videoAlbumService.lambdaQuery()
                .eq(VideoAlbum::getStatus, 1)
                .eq(categoryId != null, VideoAlbum::getCategoryId, categoryId)
                // VIP 课程包含显式 VIP 标记和付费课程，便于前端用同一筛选条件展示。
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

    /**
     * 查询当前用户已购课程列表。
     */
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

    /**
     * 查询课程详情和章节信息。
     */
    public AppCourseDetailVo courseDetail(Long courseId) {
        VideoAlbum album = getCourse(courseId);
        Long userId = UserContext.getUserId();
        // 课程详情允许游客查看，但学习权限需要结合登录状态、免费课、VIP 和已购记录计算。
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

    /**
     * 查询课程章节并补充学习状态。
     */
    public List<AppLessonVo> courseLessons(Long courseId) {
        VideoAlbum album = getCourse(courseId);
        List<VideoAlbumItem> items = videoAlbumItemService.lambdaQuery()
                .eq(VideoAlbumItem::getAlbumId, courseId)
                .orderByAsc(VideoAlbumItem::getSortNo)
                .orderByAsc(VideoAlbumItem::getId)
                .list();
        List<Long> lessonIds = items.stream().map(VideoAlbumItem::getVideoId).filter(Objects::nonNull).toList();
        // 一次性加载章节、收藏、下载、播放记录，避免对每个章节循环查库。
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

    /**
     * 查询章节播放页数据。
     */
    public AppLessonPlayerVo lessonPlayer(Long courseId, Long lessonId) {
        VideoAlbum album = getCourse(courseId);
        Long userId = requireUserId();
        if (!userCourseAccessService.hasAccess(userId, album)) {
            throw new BusinessException("当前课程未购买或无权限学习");
        }
        Video lesson = getCourseLesson(courseId, lessonId);
        // 播放页聚合媒体资源、脚本、播放进度和用户状态，前端可直接渲染播放器。
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

    /**
     * 根据章节 ID 查询播放器数据。
     */
    public AppLessonPlayerVo lessonPlayerByLesson(Long lessonId) {
        VideoAlbumItem albumItem = videoAlbumItemService.lambdaQuery()
                .eq(VideoAlbumItem::getVideoId, lessonId)
                // 一个章节可能被多个专辑引用，默认专辑优先用于反查课程。
                .orderByDesc(VideoAlbumItem::getIsDefaultAlbum)
                .orderByAsc(VideoAlbumItem::getSortNo)
                .last("limit 1")
                .one();
        if (albumItem == null || albumItem.getAlbumId() == null) {
            throw new BusinessException("lesson album not found");
        }
        return lessonPlayer(albumItem.getAlbumId(), lessonId);
    }

    /**
     * 查询章节逐句脚本。
     */
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

    /**
     * 批量转换课程卡片展示对象。
     */
    private List<AppCourseCardVo> toCourseCards(List<VideoAlbum> albums) {
        return albums.stream().map(this::toCourseCard).toList();
    }

    /**
     * 将课程专辑转换为课程卡片展示对象。
     */
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

    /**
     * 将分类实体转换为 App 分类对象。
     */
    private AppCourseCategoryVo toCategoryVo(Category category) {
        AppCourseCategoryVo vo = new AppCourseCategoryVo();
        vo.setId(String.valueOf(category.getId()));
        vo.setName(category.getName());
        vo.setCode(category.getCode());
        return vo;
    }

    /**
     * 将视频实体转换为章节展示对象。
     */
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

    /**
     * 将脚本片段转换为播放页句子对象。
     */
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

    /**
     * 拆分课程标签字符串。
     */
    private List<String> splitTags(String tags) {
        if (tags == null || tags.isBlank()) {
            return List.of();
        }
        return Arrays.stream(tags.split(","))
                .map(String::trim)
                .filter(item -> !item.isBlank())
                .toList();
    }

    /**
     * 判断课程是否属于 VIP 或付费课程。
     */
    private boolean isVipCourse(VideoAlbum album) {
        return "vip".equalsIgnoreCase(album.getAccessType())
                || (album.getPrice() != null && album.getPrice().signum() > 0);
    }

    /**
     * 将秒数格式化为分钟和秒。
     */
    private String formatDuration(Integer durationSeconds) {
        if (durationSeconds == null || durationSeconds <= 0) {
            return null;
        }
        int minutes = durationSeconds / 60;
        int seconds = durationSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    /**
     * 获取当前登录用户 ID，未登录时抛出业务异常。
     */
    private Long requireUserId() {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException("请先登录");
        }
        return userId;
    }

    /**
     * 查询并校验课程是否可用。
     */
    private VideoAlbum getCourse(Long courseId) {
        VideoAlbum album = videoAlbumService.getById(courseId);
        if (album == null || album.getStatus() == null || album.getStatus() != 1) {
            throw new BusinessException("课程不存在");
        }
        return album;
    }

    /**
     * 查询并校验课程章节是否可用。
     */
    private Video getCourseLesson(Long courseId, Long lessonId) {
        // 先验证章节归属，再读取章节本身，防止越权访问其他课程下的视频。
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
