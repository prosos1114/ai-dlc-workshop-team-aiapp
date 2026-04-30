package com.tableorder.core.config;

import com.tableorder.core.interceptor.StoreAccessInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final StoreAccessInterceptor storeAccessInterceptor;

    public WebConfig(StoreAccessInterceptor storeAccessInterceptor) {
        this.storeAccessInterceptor = storeAccessInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(storeAccessInterceptor)
                .addPathPatterns("/api/stores/{storeId}/**")
                .excludePathPatterns(
                        "/api/admin/auth/**",
                        "/api/table/auth/**",
                        "/api/stores"
                );
    }
}
