package com.englishdatamanager.backend.controller.open;

import com.englishdatamanager.backend.common.ApiResponse;
import com.englishdatamanager.backend.dto.PaymentCallbackRequest;
import com.englishdatamanager.backend.service.UserOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Open Payment")
@RestController
@RequestMapping("/api/open/payment")
@RequiredArgsConstructor
public class OpenPaymentController {

    private final UserOrderService userOrderService;

    @Operation(summary = "Payment callback")
    @PostMapping("/callback")
    public ApiResponse<Void> callback(@Valid @RequestBody PaymentCallbackRequest request) {
        if ("SUCCESS".equalsIgnoreCase(request.getPayStatus())) {
            userOrderService.handlePaymentSuccess(
                    request.getOrderNo(),
                    request.getTransactionNo(),
                    request.getPayChannel(),
                    request.getAmount()
            );
        }
        return ApiResponse.success();
    }
}
