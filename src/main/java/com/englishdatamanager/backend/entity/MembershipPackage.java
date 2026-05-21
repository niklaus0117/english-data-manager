package com.englishdatamanager.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("membership_package")
public class MembershipPackage extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String packageName;
    private String packageCode;
    private BigDecimal price;
    private Integer durationDays;
    private Integer sortNo;
    private Integer status;
    private String description;
}
