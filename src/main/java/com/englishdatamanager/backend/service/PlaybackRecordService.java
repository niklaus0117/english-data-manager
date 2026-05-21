package com.englishdatamanager.backend.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.englishdatamanager.backend.entity.PlaybackRecord;
import com.englishdatamanager.backend.mapper.PlaybackRecordMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PlaybackRecordService extends ServiceImpl<PlaybackRecordMapper, PlaybackRecord> {

    public boolean saveOrUpdateRecord(Long userId,
                                      Long videoId,
                                      Long albumId,
                                      Integer progressSeconds,
                                      Integer lastSegmentNo,
                                      Double playbackRate,
                                      Integer finished,
                                      String deviceId) {
        PlaybackRecord record = lambdaQuery()
                .eq(PlaybackRecord::getUserId, userId)
                .eq(PlaybackRecord::getVideoId, videoId)
                .one();
        if (record == null) {
            record = new PlaybackRecord();
            record.setUserId(userId);
            record.setVideoId(videoId);
        }
        record.setAlbumId(albumId);
        record.setProgressSeconds(progressSeconds);
        record.setLastSegmentNo(lastSegmentNo);
        record.setPlaybackRate(playbackRate == null ? 1.0 : playbackRate);
        record.setFinished(finished);
        record.setDeviceId(deviceId);
        record.setLastPlayedAt(LocalDateTime.now());
        return saveOrUpdate(record);
    }
}
