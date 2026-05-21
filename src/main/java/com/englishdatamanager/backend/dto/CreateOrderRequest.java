package com.englishdatamanager.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateOrderRequest {

    @NotNull(message = "packageId is required")
    private Long packageId;
}
