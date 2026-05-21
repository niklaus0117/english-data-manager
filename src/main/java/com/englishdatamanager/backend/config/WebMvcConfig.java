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

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String path = "file:" + appProperties.getStorage().getLocalPath() + "/";
        registry.addResourceHandler(appProperties.getStorage().getPublicPrefix() + "**")
                .addResourceLocations(path);
    }
}
