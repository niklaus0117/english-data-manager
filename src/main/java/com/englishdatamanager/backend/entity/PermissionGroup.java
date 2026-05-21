package com.englishdatamanager.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("permission_group")
public class PermissionGroup extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String groupName;
    private String groupCode;
    private Integer status;
    private String featureCodes;
    private String description;
}
