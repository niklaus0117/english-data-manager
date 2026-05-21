package com.englishdatamanager.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentCallbackRequest {

    @NotBlank(message = "orderNo is required")
    private String orderNo;

    @NotBlank(message = "transactionNo is required")
    private String transactionNo;

    @NotBlank(message = "payChannel is required")
    private String payChannel;

    @NotNull(message = "amount is required")
    private BigDecimal amount;

    private String payStatus = "SUCCESS";
}
