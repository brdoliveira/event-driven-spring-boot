package com.appsdeveloperblog.payments.service.handler;

import com.appsdeveloperblog.core.dto.Payment;
import com.appsdeveloperblog.core.dto.commands.ProcessPaymentCommand;
import com.appsdeveloperblog.core.dto.events.PaymentFailedEvent;
import com.appsdeveloperblog.core.dto.events.PaymentProcessedEvent;
import com.appsdeveloperblog.core.exceptions.CreditCardProcessorUnavailableException;
import com.appsdeveloperblog.payments.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentsCommandsHandlerTest {

    private static final String PAYMENT_EVENTS_TOPIC = "payment-events";

    @Mock
    private PaymentService paymentService;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    private PaymentsCommandsHandler paymentsCommandsHandler;

    @BeforeEach
    void setUp() {
        paymentsCommandsHandler = new PaymentsCommandsHandler(paymentService, kafkaTemplate, PAYMENT_EVENTS_TOPIC);
    }

    @Test
    @DisplayName("@spec:AC-013 successful payment publishes the original order and generated payment identifiers")
    void successfulPaymentPublishesPaymentProcessedEvent() {
        UUID orderId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        UUID paymentId = UUID.randomUUID();
        Integer quantity = 2;
        BigDecimal price = new BigDecimal("24.95");
        ProcessPaymentCommand command = new ProcessPaymentCommand(orderId, productId, price, quantity);
        Payment processedPayment = new Payment(paymentId, orderId, productId, price, quantity);
        when(paymentService.process(any(Payment.class))).thenReturn(processedPayment);

        paymentsCommandsHandler.handleCommand(command);

        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentService).process(paymentCaptor.capture());
        Payment payment = paymentCaptor.getValue();
        assertEquals(orderId, payment.getOrderId());
        assertEquals(productId, payment.getProductId());
        assertEquals(quantity, payment.getProductQuantity());
        assertEquals(price, payment.getProductPrice());

        ArgumentCaptor<PaymentProcessedEvent> eventCaptor = ArgumentCaptor.forClass(PaymentProcessedEvent.class);
        verify(kafkaTemplate).send(eq(PAYMENT_EVENTS_TOPIC), eventCaptor.capture());

        PaymentProcessedEvent event = eventCaptor.getValue();
        assertEquals(orderId, event.getOrderId());
        assertEquals(paymentId, event.getPaymentId());
    }

    @Test
    @DisplayName("@spec:AC-013 unavailable credit-card processor publishes the original order, product, and quantity")
    void unavailableCreditCardProcessorPublishesPaymentFailedEvent() {
        UUID orderId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        Integer quantity = 2;
        BigDecimal price = new BigDecimal("24.95");
        ProcessPaymentCommand command = new ProcessPaymentCommand(orderId, productId, price, quantity);
        when(paymentService.process(any(Payment.class)))
                .thenThrow(new CreditCardProcessorUnavailableException(new IllegalStateException("processor offline")));

        paymentsCommandsHandler.handleCommand(command);

        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentService).process(paymentCaptor.capture());
        Payment payment = paymentCaptor.getValue();
        assertEquals(orderId, payment.getOrderId());
        assertEquals(productId, payment.getProductId());
        assertEquals(quantity, payment.getProductQuantity());
        assertEquals(price, payment.getProductPrice());

        ArgumentCaptor<PaymentFailedEvent> eventCaptor = ArgumentCaptor.forClass(PaymentFailedEvent.class);
        verify(kafkaTemplate).send(eq(PAYMENT_EVENTS_TOPIC), eventCaptor.capture());

        PaymentFailedEvent event = eventCaptor.getValue();
        assertEquals(orderId, event.getOrderId());
        assertEquals(productId, event.getProductId());
        assertEquals(quantity, event.getProductQuantity());
    }
}
