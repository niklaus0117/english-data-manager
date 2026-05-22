package com.englishdatamanager.backend.security;

import com.englishdatamanager.backend.config.AppProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class TokenService {

    private static final String TOKEN_PREFIX = "token:";

    private final StringRedisTemplate stringRedisTemplate;
    private final AppProperties appProperties;
    private final ConcurrentMap<String, LocalToken> localTokenStore = new ConcurrentHashMap<>();

    /**
     * 为登录用户创建 token 并写入缓存。
     */
    public String createToken(AuthUser authUser) {
        String token = UUID.randomUUID().toString().replace("-", "");
        // payload 保持轻量，既能支撑鉴权，也避免每次请求都查询用户表。
        String payload = String.join("|",
                authUser.getUserType(),
                String.valueOf(authUser.getUserId()),
                authUser.getDisplayName() == null ? "" : authUser.getDisplayName(),
                String.valueOf(authUser.getStatus() == null ? 1 : authUser.getStatus()),
                String.valueOf(authUser.getGroupId() == null ? 0L : authUser.getGroupId()));
        // 先写本地缓存，再写 Redis；Redis 不可用时仍能支持本实例内的登录态。
        cacheTokenLocally(token, payload);
        try {
            stringRedisTemplate.opsForValue().set(
                    TOKEN_PREFIX + token,
                    payload,
                    Duration.ofSeconds(appProperties.getAuth().getTokenExpireSeconds())
            );
        } catch (RedisConnectionFailureException | RedisSystemException exception) {
            log.warn("redis unavailable when creating token, fallback to local memory store: {}", exception.getMessage());
        }
        return token;
    }

    /**
     * 根据 token 解析当前登录用户信息。
     */
    public AuthUser parseToken(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }
        String payload = null;
        try {
            payload = stringRedisTemplate.opsForValue().get(TOKEN_PREFIX + token);
        } catch (RedisConnectionFailureException | RedisSystemException exception) {
            log.warn("redis unavailable when parsing token, fallback to local memory store: {}", exception.getMessage());
        }
        if (payload == null || payload.isBlank()) {
            // Redis 读取失败或 token 不存在时，使用本地缓存兜底。
            payload = getLocalTokenPayload(token);
        }
        if (payload == null || payload.isBlank()) {
            return null;
        }
        String[] items = payload.split("\\|", -1);
        if (items.length < 5) {
            return null;
        }
        return new AuthUser(
                Long.valueOf(items[1]),
                items[0],
                items[2],
                Integer.valueOf(items[3]),
                Long.valueOf(items[4])
        );
    }

    /**
     * 移除 token 并使当前登录态失效。
     */
    public void removeToken(String token) {
        if (token == null || token.isBlank()) {
            return;
        }
        localTokenStore.remove(token);
        try {
            stringRedisTemplate.delete(TOKEN_PREFIX + token);
        } catch (RedisConnectionFailureException | RedisSystemException exception) {
            log.warn("redis unavailable when removing token, local token cache has been cleared: {}", exception.getMessage());
        }
    }

    /**
     * 从 Authorization 请求头中提取实际 token。
     */
    public String resolveToken(String authorization) {
        if (authorization == null || authorization.isBlank()) {
            return null;
        }
        return authorization.startsWith("Bearer ") ? authorization.substring(7) : authorization;
    }

    /**
     * 将 token 负载写入本地内存兜底缓存。
     */
    private void cacheTokenLocally(String token, String payload) {
        Instant expiresAt = Instant.now().plusSeconds(appProperties.getAuth().getTokenExpireSeconds());
        localTokenStore.put(token, new LocalToken(payload, expiresAt));
    }

    /**
     * 从本地兜底缓存读取未过期的 token 负载。
     */
    private String getLocalTokenPayload(String token) {
        LocalToken localToken = localTokenStore.get(token);
        if (localToken == null) {
            return null;
        }
        if (localToken.expiresAt().isBefore(Instant.now())) {
            // 惰性清理过期 token，避免本地兜底缓存长期增长。
            localTokenStore.remove(token, localToken);
            return null;
        }
        return localToken.payload();
    }

    private record LocalToken(String payload, Instant expiresAt) {
    }
}
