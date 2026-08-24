package com.appsdeveloperblog.payments.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableConfigurationProperties(PaymentProcessorProperties.class)
public class ApplicationConfig {

    @Bean
    public RestTemplate ccpRestTemplate(RestTemplateBuilder restTemplateBuilder,
                                        PaymentProcessorProperties paymentProcessorProperties) {
        return restTemplateBuilder
                .setConnectTimeout(paymentProcessorProperties.getConnectTimeout())
                .setReadTimeout(paymentProcessorProperties.getReadTimeout())
                .build();
    }
}
