package com.englishdatamanager.backend.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.englishdatamanager.backend.entity.AdminMenu;
import com.englishdatamanager.backend.mapper.AdminMenuMapper;
import org.springframework.stereotype.Service;

@Service
public class AdminMenuService extends ServiceImpl<AdminMenuMapper, AdminMenu> {
}
