package com.englishdatamanager.backend.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.englishdatamanager.backend.entity.Video;
import com.englishdatamanager.backend.mapper.VideoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VideoService extends ServiceImpl<VideoMapper, Video> {

    private final KafkaEventService kafkaEventService;

    /**
     * 创建视频并发布视频变更事件。
     */
    public boolean createVideo(Video video) {
        boolean success = this.save(video);
        if (success) {
            kafkaEventService.publishVideoChanged(video.getId(), "CREATED");
        }
        return success;
    }

    /**
     * 更新视频并发布视频变更事件。
     */
    public boolean updateVideo(Video video) {
        boolean success = this.updateById(video);
        if (success) {
            kafkaEventService.publishVideoChanged(video.getId(), "UPDATED");
        }
        return success;
    }
}
