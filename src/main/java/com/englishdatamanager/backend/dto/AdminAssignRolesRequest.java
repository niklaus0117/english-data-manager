package com.englishdatamanager.backend.dto;

import lombok.Data;

import java.util.List;

@Data
public class AdminAssignRolesRequest {

    private Long adminUserId;
    private List<Long> roleIds;
}
