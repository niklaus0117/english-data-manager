package com.englishdatamanager.backend.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.englishdatamanager.backend.entity.AppUser;
import com.englishdatamanager.backend.entity.MembershipPackage;
import com.englishdatamanager.backend.entity.UserOrder;
import com.englishdatamanager.backend.entity.VideoAlbum;
import com.englishdatamanager.backend.exception.BusinessException;
import com.englishdatamanager.backend.mapper.AppUserMapper;
import com.englishdatamanager.backend.mapper.UserOrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class UserOrderService extends ServiceImpl<UserOrderMapper, UserOrder> {

    private final AppUserMapper appUserMapper;
    private final MembershipPackageService membershipPackageService;
    private final VideoAlbumService videoAlbumService;

    public Map<String, Object> createMembershipOrder(Long userId, Long packageId) {
        MembershipPackage membershipPackage = membershipPackageService.getById(packageId);
        if (membershipPackage == null || membershipPackage.getStatus() == null || membershipPackage.getStatus() != 1) {
            throw new BusinessException("套餐不存在或不可下单");
        }
        UserOrder order = new UserOrder();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setPackageId(membershipPackage.getId());
        order.setOrderType("VIP_PACKAGE");
        order.setAmount(membershipPackage.getPrice());
        order.setCurrency("CNY");
        order.setPayStatus(0);
        save(order);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("orderId", order.getId());
        result.put("orderNo", order.getOrderNo());
        result.put("amount", order.getAmount());
        result.put("currency", order.getCurrency());
        result.put("packageId", membershipPackage.getId());
        result.put("packageName", membershipPackage.getPackageName());
        return result;
    }

    public void handlePaymentSuccess(String orderNo, String transactionNo, String payChannel, BigDecimal amount) {
        UserOrder order = lambdaQuery()
                .eq(UserOrder::getOrderNo, orderNo)
                .one();
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (amount != null && order.getAmount() != null && order.getAmount().compareTo(amount) != 0) {
            throw new BusinessException("支付金额不匹配");
        }
        if (order.getPayStatus() != null && order.getPayStatus() == 1) {
            return;
        }
        order.setPayStatus(1);
        order.setTransactionNo(transactionNo);
        order.setPayChannel(payChannel);
        order.setPaidAt(LocalDateTime.now());
        updateById(order);

        if ("VIP_PACKAGE".equalsIgnoreCase(order.getOrderType())) {
            AppUser appUser = appUserMapper.selectById(order.getUserId());
            MembershipPackage membershipPackage = order.getPackageId() == null
                    ? null
                    : membershipPackageService.getById(order.getPackageId());
            if (appUser != null) {
                LocalDateTime baseTime = appUser.getVipExpireAt() != null && appUser.getVipExpireAt().isAfter(LocalDateTime.now())
                        ? appUser.getVipExpireAt()
                        : LocalDateTime.now();
                int durationDays = membershipPackage == null || membershipPackage.getDurationDays() == null
                        ? 30
                        : membershipPackage.getDurationDays();
                appUser.setPermissionGroupId(2L);
                appUser.setVipExpireAt(baseTime.plusDays(durationDays));
                appUserMapper.updateById(appUser);
            }
        }
    }

    public Map<String, Object> createCourseOrder(Long userId, Long courseId) {
        VideoAlbum course = videoAlbumService.getById(courseId);
        if (course == null || course.getStatus() == null || course.getStatus() != 1) {
            throw new BusinessException("课程不存在或不可下单");
        }
        UserOrder order = new UserOrder();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setCourseId(course.getId());
        order.setCourseTitle(course.getTitle());
        order.setOrderType("COURSE_PURCHASE");
        order.setAmount(course.getPrice() == null ? BigDecimal.ZERO : course.getPrice());
        order.setCurrency("CNY");
        order.setPayStatus(1);
        order.setPayChannel("demo");
        order.setTransactionNo("COURSE-" + order.getOrderNo());
        order.setPaidAt(LocalDateTime.now());
        save(order);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("orderId", order.getId());
        result.put("orderNo", order.getOrderNo());
        result.put("courseId", course.getId());
        result.put("courseTitle", course.getTitle());
        result.put("amount", order.getAmount());
        return result;
    }

    private String generateOrderNo() {
        String timePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int randomPart = ThreadLocalRandom.current().nextInt(1000, 9999);
        return "ORD" + timePart + randomPart;
    }
}
