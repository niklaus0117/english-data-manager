package com.englishdatamanager.backend.security;

import com.englishdatamanager.backend.config.AppProperties;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TokenServiceTest {

    @Test
    void shouldFallbackToLocalStoreWhenRedisUnavailable() {
        StringRedisTemplate stringRedisTemplate = mock(StringRedisTemplate.class);
        @SuppressWarnings("unchecked")
        ValueOperations<String, String> valueOperations = mock(ValueOperations.class);
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        doThrow(new RedisConnectionFailureException("redis down"))
                .when(valueOperations)
                .set(anyString(), anyString(), any(Duration.class));
        when(valueOperations.get(anyString())).thenThrow(new RedisConnectionFailureException("redis down"));
        doThrow(new RedisConnectionFailureException("redis down"))
                .when(stringRedisTemplate)
                .delete(anyString());

        AppProperties appProperties = new AppProperties();
        appProperties.getAuth().setTokenExpireSeconds(60L);
        TokenService tokenService = new TokenService(stringRedisTemplate, appProperties);

        String token = tokenService.createToken(new AuthUser(1L, "ADMIN", "System Admin", 1, 0L));
        assertNotNull(token);

        AuthUser authUser = tokenService.parseToken(token);
        assertNotNull(authUser);
        assertEquals(1L, authUser.getUserId());
        assertEquals("ADMIN", authUser.getUserType());

        tokenService.removeToken(token);

        assertNull(tokenService.parseToken(token));
    }
}
