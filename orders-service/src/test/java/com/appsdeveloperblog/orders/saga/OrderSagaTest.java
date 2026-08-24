package com.appsdeveloperblog.orders.saga;

import com.appsdeveloperblog.core.dto.commands.CancelProductReservationCommand;
import com.appsdeveloperblog.core.dto.commands.ProductReservationCancelledEvent;
import com.appsdeveloperblog.core.dto.commands.RejectOrderCommand;
import com.appsdeveloperblog.core.dto.events.PaymentFailedEvent;
import com.appsdeveloperblog.core.types.OrderStatus;
import com.appsdeveloperblog.orders.service.OrderHistoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrderSagaTest {

    private static final String PRODUCTS_COMMANDS_TOPIC = "products-commands";
    private static final String PAYMENTS_COMMANDS_TOPIC = "payments-commands";
    private static final String ORDERS_COMMANDS_TOPIC = "orders-commands";

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Mock
    private OrderHistoryService orderHistoryService;

    private OrderSaga orderSaga;

    @BeforeEach
    void setUp() {
        orderSaga = new OrderSaga(
                kafkaTemplate,
                PRODUCTS_COMMANDS_TOPIC,
                orderHistoryService,
                PAYMENTS_COMMANDS_TOPIC,
                ORDERS_COMMANDS_TOPIC
        );
    }

    @Test
    @DisplayName("@spec:AC-006 payment failure publishes a reservation cancellation with the original order, product, and quantity")
    void paymentFailurePublishesReservationCancellation() {
        UUID orderId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        Integer productQuantity = 3;

        orderSaga.handleEvent(new PaymentFailedEvent(orderId, productId, productQuantity));

        ArgumentCaptor<CancelProductReservationCommand> commandCaptor =
                ArgumentCaptor.forClass(CancelProductReservationCommand.class);
        verify(kafkaTemplate).send(eq(PRODUCTS_COMMANDS_TOPIC), commandCaptor.capture());

        CancelProductReservationCommand command = commandCaptor.getValue();
        assertEquals(orderId, command.getOrderId());
        assertEquals(productId, command.getProductId());
        assertEquals(productQuantity, command.getProductQuantity());
    }

    @Test
    @DisplayName("@spec:AC-007 cancelled product reservation rejects the order and records rejected history")
    void cancelledReservationRejectsOrderAndRecordsRejectedHistory() {
        UUID orderId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        orderSaga.handleEvent(new ProductReservationCancelledEvent(productId, orderId));

        ArgumentCaptor<RejectOrderCommand> commandCaptor = ArgumentCaptor.forClass(RejectOrderCommand.class);
        verify(kafkaTemplate).send(eq(ORDERS_COMMANDS_TOPIC), commandCaptor.capture());
        verify(orderHistoryService).add(orderId, OrderStatus.REJECTED);

        assertEquals(orderId, commandCaptor.getValue().getOrderId());
    }
}
