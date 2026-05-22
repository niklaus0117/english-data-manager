package com.englishdatamanager.backend.controller.app;

import com.englishdatamanager.backend.common.ApiResponse;
import com.englishdatamanager.backend.dto.CreateOrderRequest;
import com.englishdatamanager.backend.entity.MembershipPackage;
import com.englishdatamanager.backend.entity.UserOrder;
import com.englishdatamanager.backend.security.UserContext;
import com.englishdatamanager.backend.service.MembershipPackageService;
import com.englishdatamanager.backend.service.UserOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "App Order")
@RestController
@RequestMapping("/api/app/orders")
@RequiredArgsConstructor
public class AppOrderController {

    private final MembershipPackageService membershipPackageService;
    private final UserOrderService userOrderService;

    /**
     * 查询可购买的会员套餐列表。
     */
    @Operation(summary = "Membership package list")
    @GetMapping("/packages")
    public ApiResponse<List<MembershipPackage>> packages() {
        return ApiResponse.success(membershipPackageService.lambdaQuery()
                .eq(MembershipPackage::getStatus, 1)
                .orderByAsc(MembershipPackage::getSortNo)
                .orderByAsc(MembershipPackage::getId)
                .list());
    }

    /**
     * 创建一条业务数据。
     */
    @Operation(summary = "Create membership order")
    @PostMapping
    public ApiResponse<Map<String, Object>> create(@Valid @RequestBody CreateOrderRequest request) {
        return ApiResponse.success(userOrderService.createMembershipOrder(UserContext.getUserId(), request.getPackageId()));
    }

    /**
     * 查询当前用户订单列表。
     */
    @Operation(summary = "My order list")
    @GetMapping
    public ApiResponse<List<UserOrder>> myOrders() {
        return ApiResponse.success(userOrderService.lambdaQuery()
                .eq(UserOrder::getUserId, UserContext.getUserId())
                .orderByDesc(UserOrder::getId)
                .list());
    }

    /**
     * 查询数据详情。
     */
    @Operation(summary = "My order detail")
    @GetMapping("/{id}")
    public ApiResponse<UserOrder> detail(@PathVariable Long id) {
        return ApiResponse.success(userOrderService.lambdaQuery()
                .eq(UserOrder::getId, id)
                .eq(UserOrder::getUserId, UserContext.getUserId())
                .one());
    }
}
