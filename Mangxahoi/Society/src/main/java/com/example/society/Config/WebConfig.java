package com.example.society.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Cấu hình CORS cho WebSocket endpoint /ws
        registry.addMapping("/ws/**")
                .allowedOrigins("http://localhost:8080")  // Cho phép kết nối từ mọi domain
                .allowedMethods("*")  // Cho phép phương thức GET và POST
                .allowedHeaders("*");  // Cho phép mọi header
    }
}