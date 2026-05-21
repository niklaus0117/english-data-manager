package com.englishdatamanager.backend.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.englishdatamanager.backend.entity.VideoContent;
import com.englishdatamanager.backend.mapper.VideoContentMapper;
import org.springframework.stereotype.Service;

@Service
public class VideoContentService extends ServiceImpl<VideoContentMapper, VideoContent> {
}
