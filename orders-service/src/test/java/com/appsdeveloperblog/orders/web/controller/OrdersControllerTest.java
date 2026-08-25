package com.appsdeveloperblog.orders.web.controller;

import com.appsdeveloperblog.core.dto.Order;
import com.appsdeveloperblog.core.types.OrderStatus;
import com.appsdeveloperblog.orders.service.OrderHistoryService;
import com.appsdeveloperblog.orders.service.OrderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrdersController.class)
class OrdersControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @MockBean
    private OrderHistoryService orderHistoryService;

    @Test
    @DisplayName("@spec:AC-008 pedido válido é aceito com os dados criados")
    void acceptsValidOrder() throws Exception {
        UUID orderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        when(orderService.placeOrder(argThat(order ->
                customerId.equals(order.getCustomerId())
                        && productId.equals(order.getProductId())
                        && Integer.valueOf(2).equals(order.getProductQuantity()))))
                .thenReturn(new Order(orderId, customerId, productId, 2, OrderStatus.CREATED));

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"customerId\":\"" + customerId + "\",\"productId\":\"" + productId + "\",\"productQuantity\":2}"))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.orderId").value(orderId.toString()))
                .andExpect(jsonPath("$.customerId").value(customerId.toString()))
                .andExpect(jsonPath("$.productId").value(productId.toString()))
                .andExpect(jsonPath("$.productQuantity").value(2))
                .andExpect(jsonPath("$.status").value("CREATED"));
    }

    @Test
    @DisplayName("@spec:AC-009 pedido inválido é rejeitado antes do serviço")
    void rejectsInvalidOrdersWithoutCallingService() throws Exception {
        UUID productId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productId\":\"" + productId + "\",\"productQuantity\":1}"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"customerId\":\"" + customerId + "\",\"productId\":\"" + productId + "\",\"productQuantity\":0}"))
                .andExpect(status().isBadRequest());

        verify(orderService, never()).placeOrder(any(Order.class));
    }
}
