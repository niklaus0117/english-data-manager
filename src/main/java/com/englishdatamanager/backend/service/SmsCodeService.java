package com.englishdatamanager.backend.service;

import com.englishdatamanager.backend.config.AppProperties;
import com.englishdatamanager.backend.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class SmsCodeService {

    private static final String SMS_CODE_PREFIX = "sms:code:";

    private final StringRedisTemplate stringRedisTemplate;
    private final AppProperties appProperties;

    public Map<String, Object> sendCode(String mobile) {
        String code = generateCode();
        stringRedisTemplate.opsForValue().set(
                SMS_CODE_PREFIX + mobile,
                code,
                Duration.ofSeconds(appProperties.getSms().getCodeExpireSeconds())
        );
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("mobile", mobile);
        result.put("expireSeconds", appProperties.getSms().getCodeExpireSeconds());
        result.put("mockEnabled", appProperties.getSms().isMockEnabled());
        if (appProperties.getSms().isMockEnabled()) {
            result.put("code", code);
        }
        return result;
    }

    public void verifyCode(String mobile, String code) {
        String cachedCode = stringRedisTemplate.opsForValue().get(SMS_CODE_PREFIX + mobile);
        if (cachedCode == null || !cachedCode.equals(code)) {
            throw new BusinessException("验证码错误或已过期");
        }
        stringRedisTemplate.delete(SMS_CODE_PREFIX + mobile);
    }

    private String generateCode() {
        int random = ThreadLocalRandom.current().nextInt(100000, 999999);
        if (!appProperties.getSms().isMockEnabled()) {
            return String.valueOf(random);
        }
        String prefix = appProperties.getSms().getMockPrefix();
        String suffix = String.valueOf(random);
        return suffix.substring(Math.max(0, suffix.length() - 6));
    }
}
