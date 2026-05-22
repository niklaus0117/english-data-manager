package com.englishdatamanager.backend.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.englishdatamanager.backend.entity.UserDownloadRecord;
import com.englishdatamanager.backend.mapper.UserDownloadRecordMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserDownloadRecordService extends ServiceImpl<UserDownloadRecordMapper, UserDownloadRecord> {

    /**
     * 记录用户已下载的视频资源。
     */
    public void markDownloaded(Long userId, Long videoId, String downloadUrl, Long fileSizeBytes) {
        UserDownloadRecord record = lambdaQuery()
                .eq(UserDownloadRecord::getUserId, userId)
                .eq(UserDownloadRecord::getVideoId, videoId)
                .one();
        if (record == null) {
            record = new UserDownloadRecord();
            record.setUserId(userId);
            record.setVideoId(videoId);
        }
        record.setStatus("done");
        record.setDownloadUrl(downloadUrl);
        record.setFileSizeBytes(fileSizeBytes);
        record.setExpiredAt(LocalDateTime.now().plusDays(30));
        saveOrUpdate(record);
    }
}
