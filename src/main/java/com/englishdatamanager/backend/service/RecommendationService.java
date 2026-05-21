package com.englishdatamanager.backend.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.englishdatamanager.backend.entity.Recommendation;
import com.englishdatamanager.backend.mapper.RecommendationMapper;
import org.springframework.stereotype.Service;

@Service
public class RecommendationService extends ServiceImpl<RecommendationMapper, Recommendation> {
}
