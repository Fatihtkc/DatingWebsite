package com.datingapp.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin("*"); // Tüm domainlere izin verin (gereksinime göre kısıtlayabilirsiniz)
        corsConfiguration.addAllowedMethod("*"); // Tüm HTTP metotlarına izin verin (gereksinime göre kısıtlayabilirsiniz)
        corsConfiguration.addAllowedHeader("*"); // Tüm başlıklara izin verin (gereksinime göre kısıtlayabilirsiniz)
        corsConfiguration.setAllowCredentials(true);  // Allow credentials

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);

        return new CorsFilter(source);
    }
}
