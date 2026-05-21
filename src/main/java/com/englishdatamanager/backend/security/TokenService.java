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

    public String createToken(AuthUser authUser) {
        String token = UUID.randomUUID().toString().replace("-", "");
        String payload = String.join("|",
                authUser.getUserType(),
                String.valueOf(authUser.getUserId()),
                authUser.getDisplayName() == null ? "" : authUser.getDisplayName(),
                String.valueOf(authUser.getStatus() == null ? 1 : authUser.getStatus()),
                String.valueOf(authUser.getGroupId() == null ? 0L : authUser.getGroupId()));
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

    public String resolveToken(String authorization) {
        if (authorization == null || authorization.isBlank()) {
            return null;
        }
        return authorization.startsWith("Bearer ") ? authorization.substring(7) : authorization;
    }

    private void cacheTokenLocally(String token, String payload) {
        Instant expiresAt = Instant.now().plusSeconds(appProperties.getAuth().getTokenExpireSeconds());
        localTokenStore.put(token, new LocalToken(payload, expiresAt));
    }

    private String getLocalTokenPayload(String token) {
        LocalToken localToken = localTokenStore.get(token);
        if (localToken == null) {
            return null;
        }
        if (localToken.expiresAt().isBefore(Instant.now())) {
            localTokenStore.remove(token, localToken);
            return null;
        }
        return localToken.payload();
    }

    private record LocalToken(String payload, Instant expiresAt) {
    }
}
