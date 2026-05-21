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

    public String normalizeTargetType(String targetType) {
        if (targetType == null || targetType.isBlank()) {
            return TARGET_TYPE_VIDEO;
        }
        return targetType.trim().toLowerCase();
    }

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

    public void addFavorite(Long userId, String targetType, Long targetId) {
        if (userId == null || targetId == null) {
            return;
        }
        String normalizedType = normalizeTargetType(targetType);
        if (!isFavorited(userId, normalizedType, targetId)) {
            UserFavorite favorite = new UserFavorite();
            favorite.setUserId(userId);
            favorite.setTargetType(normalizedType);
            favorite.setTargetId(targetId);
            save(favorite);
        }
    }

    public void removeFavorite(Long userId, String targetType, Long targetId) {
        if (userId == null || targetId == null) {
            return;
        }
        lambdaUpdate()
                .eq(UserFavorite::getUserId, userId)
                .eq(UserFavorite::getTargetType, normalizeTargetType(targetType))
                .eq(UserFavorite::getTargetId, targetId)
                .remove();
    }

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
