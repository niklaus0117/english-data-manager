package com.englishdatamanager.backend.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.englishdatamanager.backend.entity.UserNote;
import com.englishdatamanager.backend.mapper.UserNoteMapper;
import org.springframework.stereotype.Service;

@Service
public class UserNoteService extends ServiceImpl<UserNoteMapper, UserNote> {
}
