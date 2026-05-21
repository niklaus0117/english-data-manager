package com.englishdatamanager.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("recommendation")
public class Recommendation extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String positionCode;
    private Long videoId;
    private String imageUrl;
    private String jumpType;
    private String jumpValue;
    private Integer sortNo;
    private Integer status;
}
