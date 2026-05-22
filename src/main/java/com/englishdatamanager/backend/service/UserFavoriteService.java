package com.englishdatamanager.backend.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.englishdatamanager.backend.entity.UserFavorite;
import com.englishdatamanager.backend.mapper.UserFavoriteMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserFavoriteService extends ServiceImpl<UserFavoriteMapper, UserFavorite> {

    public static final String TARGET_TYPE_VIDEO = "video";
    public static final String TARGET_TYPE_ALBUM = "album";

    /**
     * 规范化收藏目标类型。
     */
    public String normalizeTargetType(String targetType) {
        if (targetType == null || targetType.isBlank()) {
            return TARGET_TYPE_VIDEO;
        }
        return targetType.trim().toLowerCase();
    }

    /**
     * 判断目标是否已被用户收藏。
     */
    public boolean isFavorited(Long userId, String targetType, Long targetId) {
        if (userId == null || targetId == null) {
            return false;
        }
        return lambdaQuery()
                .eq(UserFavorite::getUserId, userId)
                .eq(UserFavorite::getTargetType, normalizeTargetType(targetType))
                .eq(UserFavorite::getTargetId, targetId)
                .exists();
    }

    /**
     * 新增当前用户收藏记录。
     */
    public void addFavorite(Long userId, String targetType, Long targetId) {
        if (userId == null || targetId == null) {
            return;
        }
        String normalizedType = normalizeTargetType(targetType);
        if (!isFavorited(userId, normalizedType, targetId)) {
            // 收藏表以 userId + targetType + targetId 表达多类型收藏，写入前先查重。
            UserFavorite favorite = new UserFavorite();
            favorite.setUserId(userId);
            favorite.setTargetType(normalizedType);
            favorite.setTargetId(targetId);
            save(favorite);
        }
    }

    /**
     * 移除收藏记录。
     */
    public void removeFavorite(Long userId, String targetType, Long targetId) {
        if (userId == null || targetId == null) {
            return;
        }
        // 取消收藏按复合条件删除，避免误删用户对其他类型目标的收藏。
        lambdaUpdate()
                .eq(UserFavorite::getUserId, userId)
                .eq(UserFavorite::getTargetType, normalizeTargetType(targetType))
                .eq(UserFavorite::getTargetId, targetId)
                .remove();
    }

    /**
     * 查询用户收藏的目标 ID 列表。
     */
    public List<Long> listTargetIds(Long userId, String targetType) {
        return lambdaQuery()
                .eq(UserFavorite::getUserId, userId)
                .eq(UserFavorite::getTargetType, normalizeTargetType(targetType))
                .list()
                .stream()
                .map(UserFavorite::getTargetId)
                .filter(java.util.Objects::nonNull)
                .toList();
    }
}
