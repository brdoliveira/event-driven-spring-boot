package com.appsdeveloperblog.payments.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.URL;
import org.hibernate.validator.constraints.time.DurationMin;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Validated
@ConfigurationProperties(prefix = "remote.ccp")
public class PaymentProcessorProperties {

    @NotBlank
    @URL
    private String url;

    @NotBlank
    @Pattern(regexp = "\\d{13,19}")
    private String sampleCreditCardNumber;

    @NotNull
    @DurationMin(seconds = 1)
    private Duration connectTimeout;

    @NotNull
    @DurationMin(seconds = 1)
    private Duration readTimeout;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSampleCreditCardNumber() {
        return sampleCreditCardNumber;
    }

    public void setSampleCreditCardNumber(String sampleCreditCardNumber) {
        this.sampleCreditCardNumber = sampleCreditCardNumber;
    }

    public Duration getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(Duration connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public Duration getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(Duration readTimeout) {
        this.readTimeout = readTimeout;
    }
}
