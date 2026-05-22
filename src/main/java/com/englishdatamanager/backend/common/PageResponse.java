package com.englishdatamanager.backend.common;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {

    private long current;
    private long size;
    private long total;
    private List<T> records;

    /**
     * 根据 MyBatis-Plus 分页对象构造统一分页响应。
     */
    public static <T> PageResponse<T> of(IPage<T> page) {
        return new PageResponse<>(page.getCurrent(), page.getSize(), page.getTotal(), page.getRecords());
    }
}
