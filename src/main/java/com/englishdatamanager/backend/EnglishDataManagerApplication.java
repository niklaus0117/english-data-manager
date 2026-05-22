package com.englishdatamanager.backend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@MapperScan("com.englishdatamanager.backend.mapper")
@ConfigurationPropertiesScan
public class EnglishDataManagerApplication {

    /**
     * 启动 Spring Boot 应用。
     */
    public static void main(String[] args) {
        SpringApplication.run(EnglishDataManagerApplication.class, args);
    }
}
