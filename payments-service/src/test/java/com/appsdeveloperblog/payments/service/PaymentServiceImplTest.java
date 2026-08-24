package com.appsdeveloperblog.payments.service;

import com.appsdeveloperblog.core.dto.Payment;
import com.appsdeveloperblog.payments.config.PaymentProcessorProperties;
import com.appsdeveloperblog.payments.dao.jpa.entity.PaymentEntity;
import com.appsdeveloperblog.payments.dao.jpa.repository.PaymentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PaymentServiceImplTest {

    @Test
    @DisplayName("@spec:AC-005")
    void sendsTheConfiguredSampleCreditCardNumberToTheProcessor() {
        var paymentRepository = mock(PaymentRepository.class);
        var ccpRemoteService = mock(CreditCardProcessorRemoteService.class);
        var properties = new PaymentProcessorProperties();
        properties.setSampleCreditCardNumber("4111111111111111");
        var payment = new Payment(UUID.randomUUID(), UUID.randomUUID(), new BigDecimal("12.50"), 2);
        when(paymentRepository.save(any(PaymentEntity.class))).thenAnswer(invocation -> {
            PaymentEntity savedPayment = invocation.getArgument(0);
            savedPayment.setId(UUID.randomUUID());
            return savedPayment;
        });
        var paymentService = new PaymentServiceImpl(paymentRepository, ccpRemoteService, properties);

        paymentService.process(payment);

        var cardNumber = ArgumentCaptor.forClass(BigInteger.class);
        verify(ccpRemoteService).process(cardNumber.capture(), eq(new BigDecimal("25.00")));
        assertThat(cardNumber.getValue()).isEqualTo(new BigInteger("4111111111111111"));
    }
}
