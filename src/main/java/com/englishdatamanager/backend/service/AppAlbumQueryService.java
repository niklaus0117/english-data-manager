package com.englishdatamanager.backend.service;

import com.englishdatamanager.backend.entity.Category;
import com.englishdatamanager.backend.entity.PlaybackRecord;
import com.englishdatamanager.backend.entity.Video;
import com.englishdatamanager.backend.entity.VideoAlbum;
import com.englishdatamanager.backend.entity.VideoAlbumItem;
import com.englishdatamanager.backend.entity.VideoSubtitleTrack;
import com.englishdatamanager.backend.exception.BusinessException;
import com.englishdatamanager.backend.security.UserContext;
import com.englishdatamanager.backend.vo.AlbumVideoItemVo;
import com.englishdatamanager.backend.vo.AppAlbumDetailVo;
import com.englishdatamanager.backend.vo.AppAlbumListVo;
import com.englishdatamanager.backend.vo.VideoAlbumCardVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppAlbumQueryService {

    private final CategoryService categoryService;
    private final VideoAlbumService videoAlbumService;
    private final VideoAlbumItemService videoAlbumItemService;
    private final VideoService videoService;
    private final PlaybackRecordService playbackRecordService;
    private final VideoSubtitleTrackService videoSubtitleTrackService;
    private final UserFavoriteService userFavoriteService;

    public AppAlbumListVo getAlbumsByCategory(Long categoryId) {
        Category category = categoryService.getById(categoryId);
        if (category == null) {
            throw new BusinessException("category not found");
        }
        List<VideoAlbum> albums = videoAlbumService.lambdaQuery()
                .eq(VideoAlbum::getCategoryId, categoryId)
                .eq(VideoAlbum::getStatus, 1)
                .orderByDesc(VideoAlbum::getIsFeatured)
                .orderByAsc(VideoAlbum::getSortNo)
                .orderByDesc(VideoAlbum::getId)
                .list();
        Set<Long> favoriteAlbumIds = loadFavoriteIds(UserFavoriteService.TARGET_TYPE_ALBUM);

        AppAlbumListVo result = new AppAlbumListVo();
        result.setCategory(category);
        result.setAlbums(albums.stream()
                .map(album -> toAlbumCardVo(album, favoriteAlbumIds.contains(album.getId())))
                .toList());
        return result;
    }

    public AppAlbumDetailVo getAlbumDetail(Long albumId) {
        VideoAlbum album = videoAlbumService.getById(albumId);
        if (album == null) {
            throw new BusinessException("album not found");
        }

        List<VideoAlbumItem> albumItems = videoAlbumItemService.lambdaQuery()
                .eq(VideoAlbumItem::getAlbumId, albumId)
                .orderByAsc(VideoAlbumItem::getSortNo)
                .orderByAsc(VideoAlbumItem::getId)
                .list();
        List<Long> videoIds = albumItems.stream()
                .map(VideoAlbumItem::getVideoId)
                .filter(Objects::nonNull)
                .toList();
        Map<Long, Video> videoMap = videoIds.isEmpty()
                ? Map.of()
                : videoService.listByIds(videoIds).stream()
                .collect(Collectors.toMap(Video::getId, Function.identity()));
        Map<Long, Boolean> translationReadyMap = videoIds.isEmpty()
                ? Map.of()
                : videoSubtitleTrackService.lambdaQuery()
                .in(VideoSubtitleTrack::getVideoId, videoIds)
                .eq(VideoSubtitleTrack::getStatus, 1)
                .list()
                .stream()
                .collect(Collectors.groupingBy(VideoSubtitleTrack::getVideoId, Collectors.collectingAndThen(
                        Collectors.counting(),
                        count -> count > 0
                )));
        Set<Long> favoriteVideoIds = loadFavoriteIds(UserFavoriteService.TARGET_TYPE_VIDEO);

        AppAlbumDetailVo result = new AppAlbumDetailVo();
        result.setAlbum(album);
        result.setFavorited(userFavoriteService.isFavorited(
                UserContext.getUserId(),
                UserFavoriteService.TARGET_TYPE_ALBUM,
                albumId
        ));
        result.setVideos(albumItems.stream()
                .map(item -> toAlbumVideoItemVo(item, videoMap.get(item.getVideoId()), translationReadyMap, favoriteVideoIds))
                .filter(Objects::nonNull)
                .toList());
        result.setContinuePlayback(findContinuePlayback(videoIds));
        return result;
    }

    private Set<Long> loadFavoriteIds(String targetType) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            return Set.of();
        }
        return userFavoriteService.listTargetIds(userId, targetType).stream().collect(Collectors.toSet());
    }

    private PlaybackRecord findContinuePlayback(List<Long> videoIds) {
        Long userId = UserContext.getUserId();
        if (userId == null || videoIds.isEmpty()) {
            return null;
        }
        return playbackRecordService.lambdaQuery()
                .eq(PlaybackRecord::getUserId, userId)
                .in(PlaybackRecord::getVideoId, videoIds)
                .orderByDesc(PlaybackRecord::getLastPlayedAt)
                .last("limit 1")
                .one();
    }

    private VideoAlbumCardVo toAlbumCardVo(VideoAlbum album, boolean favorited) {
        VideoAlbumCardVo vo = new VideoAlbumCardVo();
        vo.setAlbum(album);
        vo.setFavorited(favorited);
        return vo;
    }

    private AlbumVideoItemVo toAlbumVideoItemVo(VideoAlbumItem item,
                                                Video video,
                                                Map<Long, Boolean> translationReadyMap,
                                                Set<Long> favoriteVideoIds) {
        if (video == null) {
            return null;
        }
        AlbumVideoItemVo vo = new AlbumVideoItemVo();
        vo.setAlbumItemId(item.getId());
        vo.setSortNo(item.getSortNo());
        vo.setVideo(video);
        vo.setTranslationReady(Boolean.TRUE.equals(translationReadyMap.get(video.getId())));
        vo.setFavorited(favoriteVideoIds.contains(video.getId()));
        return vo;
    }
}
