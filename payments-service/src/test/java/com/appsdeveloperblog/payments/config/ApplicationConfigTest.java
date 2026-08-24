package com.appsdeveloperblog.payments.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ApplicationConfigTest {

    @Test
    @DisplayName("@spec:AC-004")
    void configuresRestTemplateWithConfiguredTimeouts() {
        var properties = new PaymentProcessorProperties();
        properties.setConnectTimeout(Duration.ofSeconds(2));
        properties.setReadTimeout(Duration.ofSeconds(5));
        var restTemplateBuilder = mock(RestTemplateBuilder.class);
        var restTemplate = mock(RestTemplate.class);
        when(restTemplateBuilder.setConnectTimeout(properties.getConnectTimeout())).thenReturn(restTemplateBuilder);
        when(restTemplateBuilder.setReadTimeout(properties.getReadTimeout())).thenReturn(restTemplateBuilder);
        when(restTemplateBuilder.build()).thenReturn(restTemplate);

        var configuredRestTemplate = new ApplicationConfig().ccpRestTemplate(restTemplateBuilder, properties);

        assertThat(configuredRestTemplate).isSameAs(restTemplate);
        verify(restTemplateBuilder).setConnectTimeout(Duration.ofSeconds(2));
        verify(restTemplateBuilder).setReadTimeout(Duration.ofSeconds(5));
    }
}
