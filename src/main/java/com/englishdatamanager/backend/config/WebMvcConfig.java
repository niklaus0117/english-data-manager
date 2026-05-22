package com.englishdatamanager.backend.config;

import com.englishdatamanager.backend.security.AuthInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;
    private final AppProperties appProperties;

    /**
     * 注册全局认证拦截器。
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/api/admin/auth/login",
                        "/api/auth/login",
                        "/api/auth/send-code",
                        "/api/app/auth/login",
                        "/api/app/auth/sms-code",
                        "/api/app/auth/sms-login",
                        "/api/open/**",
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html"
                );
    }

    /**
     * 注册本地静态文件访问映射。
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String path = "file:" + appProperties.getStorage().getLocalPath() + "/";
        registry.addResourceHandler(appProperties.getStorage().getPublicPrefix() + "**")
                .addResourceLocations(path);
    }
}
