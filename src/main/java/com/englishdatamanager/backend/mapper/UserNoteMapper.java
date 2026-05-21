package com.englishdatamanager.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.englishdatamanager.backend.entity.UserNote;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserNoteMapper extends BaseMapper<UserNote> {
}
