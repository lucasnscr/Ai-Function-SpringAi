package com.ai.multi_agent.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import static org.springframework.http.HttpHeaders.USER_AGENT;

@Configuration
public class WebClientConfig {

    @Value("${Alpha.host}")
    private String ALPHA_API_BASE_URL;

    @Bean
    public WebClient alphaClient(WebClient.Builder webClientBuilder) {
        return webClientBuilder
                .baseUrl(ALPHA_API_BASE_URL)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .defaultHeader(USER_AGENT, USER_AGENT)
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(-1))
                        .build())
                .build();
    }
}
