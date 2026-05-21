package com.englishdatamanager.backend.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.englishdatamanager.backend.common.ApiResponse;
import com.englishdatamanager.backend.common.PageResponse;
import com.englishdatamanager.backend.entity.UserOrder;
import com.englishdatamanager.backend.service.UserOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private final UserOrderService userOrderService;

    @GetMapping
    public ApiResponse<PageResponse<UserOrder>> page(
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) Integer payStatus,
            @RequestParam(required = false) Long userId) {
        Page<UserOrder> page = userOrderService.lambdaQuery()
                .eq(payStatus != null, UserOrder::getPayStatus, payStatus)
                .eq(userId != null, UserOrder::getUserId, userId)
                .orderByDesc(UserOrder::getId)
                .page(new Page<>(current, size));
        return ApiResponse.success(PageResponse.of(page));
    }

    @GetMapping("/{id}")
    public ApiResponse<UserOrder> detail(@PathVariable Long id) {
        return ApiResponse.success(userOrderService.getById(id));
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id, @RequestBody UserOrder order) {
        order.setId(id);
        userOrderService.updateById(order);
        return ApiResponse.success();
    }
}
