package com.englishdatamanager.backend.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.englishdatamanager.backend.entity.PermissionGroup;
import com.englishdatamanager.backend.mapper.PermissionGroupMapper;
import org.springframework.stereotype.Service;

@Service
public class PermissionGroupService extends ServiceImpl<PermissionGroupMapper, PermissionGroup> {
}
