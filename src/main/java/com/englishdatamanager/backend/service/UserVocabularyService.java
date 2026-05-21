package com.englishdatamanager.backend.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.englishdatamanager.backend.entity.UserVocabulary;
import com.englishdatamanager.backend.mapper.UserVocabularyMapper;
import org.springframework.stereotype.Service;

@Service
public class UserVocabularyService extends ServiceImpl<UserVocabularyMapper, UserVocabulary> {
}
