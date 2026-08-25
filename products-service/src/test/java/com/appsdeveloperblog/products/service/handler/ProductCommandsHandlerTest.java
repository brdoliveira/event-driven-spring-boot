package com.appsdeveloperblog.products.service.handler;

import com.appsdeveloperblog.core.dto.Product;
import com.appsdeveloperblog.core.dto.commands.CancelProductReservationCommand;
import com.appsdeveloperblog.core.dto.commands.ProductReservationCancelledEvent;
import com.appsdeveloperblog.core.dto.commands.ReserveProductCommand;
import com.appsdeveloperblog.core.dto.events.ProductReservationFailedEvent;
import com.appsdeveloperblog.core.dto.events.ProductReservedEvent;
import com.appsdeveloperblog.products.service.ProductService;
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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductCommandsHandlerTest {

    private static final String PRODUCT_EVENTS_TOPIC = "product-events";

    @Mock
    private ProductService productService;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    private ProductCommandsHandler productCommandsHandler;

    @BeforeEach
    void setUp() {
        productCommandsHandler = new ProductCommandsHandler(productService, kafkaTemplate, PRODUCT_EVENTS_TOPIC);
    }

    @Test
    @DisplayName("@spec:AC-011 successful reservation publishes the original order, product, quantity, and reserved price")
    void successfulReservationPublishesProductReservedEvent() {
        UUID orderId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        Integer quantity = 3;
        BigDecimal price = new BigDecimal("49.90");
        ReserveProductCommand command = new ReserveProductCommand(productId, quantity, orderId);
        Product reservedProduct = new Product(productId, "Keyboard", price, quantity);
        when(productService.reserve(any(Product.class), eq(orderId))).thenReturn(reservedProduct);

        productCommandsHandler.handleCommand(command);

        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productService).reserve(productCaptor.capture(), eq(orderId));
        assertEquals(productId, productCaptor.getValue().getId());
        assertEquals(quantity, productCaptor.getValue().getQuantity());

        ArgumentCaptor<ProductReservedEvent> eventCaptor = ArgumentCaptor.forClass(ProductReservedEvent.class);
        verify(kafkaTemplate).send(eq(PRODUCT_EVENTS_TOPIC), eventCaptor.capture());

        ProductReservedEvent event = eventCaptor.getValue();
        assertEquals(orderId, event.getOrderId());
        assertEquals(productId, event.getProductId());
        assertEquals(quantity, event.getProductQuantity());
        assertEquals(price, event.getProductPrice());
    }

    @Test
    @DisplayName("@spec:AC-011 failed reservation publishes the original order, product, and quantity")
    void failedReservationPublishesProductReservationFailedEvent() {
        UUID orderId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        Integer quantity = 3;
        ReserveProductCommand command = new ReserveProductCommand(productId, quantity, orderId);
        doThrow(new IllegalStateException("out of stock"))
                .when(productService).reserve(any(Product.class), eq(orderId));

        productCommandsHandler.handleCommand(command);

        ArgumentCaptor<ProductReservationFailedEvent> eventCaptor =
                ArgumentCaptor.forClass(ProductReservationFailedEvent.class);
        verify(kafkaTemplate).send(eq(PRODUCT_EVENTS_TOPIC), eventCaptor.capture());

        ProductReservationFailedEvent event = eventCaptor.getValue();
        assertEquals(orderId, event.getOrderId());
        assertEquals(productId, event.getProductId());
        assertEquals(quantity, event.getProductQuantity());
    }

    @Test
    @DisplayName("@spec:AC-012 reservation cancellation publishes confirmation with the original order and product")
    void reservationCancellationPublishesProductReservationCancelledEvent() {
        UUID orderId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        Integer quantity = 3;
        CancelProductReservationCommand command = new CancelProductReservationCommand(productId, orderId, quantity);

        productCommandsHandler.handleCommand(command);

        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productService).cancelReservation(productCaptor.capture(), eq(orderId));
        assertEquals(productId, productCaptor.getValue().getId());
        assertEquals(quantity, productCaptor.getValue().getQuantity());

        ArgumentCaptor<ProductReservationCancelledEvent> eventCaptor =
                ArgumentCaptor.forClass(ProductReservationCancelledEvent.class);
        verify(kafkaTemplate).send(eq(PRODUCT_EVENTS_TOPIC), eventCaptor.capture());

        ProductReservationCancelledEvent event = eventCaptor.getValue();
        assertEquals(orderId, event.getOrderId());
        assertEquals(productId, event.getProductId());
    }
}
