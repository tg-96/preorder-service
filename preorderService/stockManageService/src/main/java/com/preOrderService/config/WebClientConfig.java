package com.preOrderService.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    /**
     * itemService API
     */
    @Bean
    public WebClient ItemServiceClient(){
        return WebClient.builder()
                .baseUrl("http://localhost:8084")
                .build();
    }
}
