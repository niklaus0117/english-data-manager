package com.englishdatamanager.backend.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.englishdatamanager.backend.entity.SystemConfig;
import com.englishdatamanager.backend.mapper.SystemConfigMapper;
import org.springframework.stereotype.Service;

@Service
public class SystemConfigService extends ServiceImpl<SystemConfigMapper, SystemConfig> {
}
