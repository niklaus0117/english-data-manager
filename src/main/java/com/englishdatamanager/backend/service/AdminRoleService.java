package com.englishdatamanager.backend.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.englishdatamanager.backend.entity.AdminRole;
import com.englishdatamanager.backend.mapper.AdminRoleMapper;
import org.springframework.stereotype.Service;

@Service
public class AdminRoleService extends ServiceImpl<AdminRoleMapper, AdminRole> {
}
