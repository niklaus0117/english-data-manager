package com.englishdatamanager.backend.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.englishdatamanager.backend.entity.AppUser;
import com.englishdatamanager.backend.entity.UserCourseAccess;
import com.englishdatamanager.backend.entity.VideoAlbum;
import com.englishdatamanager.backend.mapper.UserCourseAccessMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserCourseAccessService extends ServiceImpl<UserCourseAccessMapper, UserCourseAccess> {

    private final AppUserService appUserService;

    /**
     * 判断用户是否拥有课程访问权限。
     */
    public boolean hasAccess(Long userId, VideoAlbum album) {
        if (album == null) {
            return false;
        }
        if (album.getPrice() == null || album.getPrice().signum() <= 0) {
            return true;
        }
        if (userId == null) {
            return false;
        }
        if (existsActiveAccess(userId, album.getId())) {
            return true;
        }
        if ("vip".equalsIgnoreCase(album.getAccessType())) {
            AppUser user = appUserService.getById(userId);
            return user != null && user.getVipExpireAt() != null && user.getVipExpireAt().isAfter(LocalDateTime.now());
        }
        return false;
    }

    /**
     * 判断用户是否已有有效课程访问记录。
     */
    public boolean existsActiveAccess(Long userId, Long courseId) {
        return lambdaQuery()
                .eq(UserCourseAccess::getUserId, userId)
                .eq(UserCourseAccess::getCourseId, courseId)
                .eq(UserCourseAccess::getStatus, 1)
                .and(wrapper -> wrapper.isNull(UserCourseAccess::getExpiredAt)
                        .or()
                        .gt(UserCourseAccess::getExpiredAt, LocalDateTime.now()))
                .exists();
    }

    /**
     * 为用户授予课程访问权限。
     */
    public void grantAccess(Long userId, Long courseId, String sourceType) {
        if (existsActiveAccess(userId, courseId)) {
            return;
        }
        UserCourseAccess access = new UserCourseAccess();
        access.setUserId(userId);
        access.setCourseId(courseId);
        access.setSourceType(sourceType);
        access.setStatus(1);
        access.setGrantedAt(LocalDateTime.now());
        save(access);
    }
}
