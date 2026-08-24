package com.appsdeveloperblog.products.web.controller;

import com.appsdeveloperblog.core.dto.Product;
import com.appsdeveloperblog.products.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductsController.class)
class ProductsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Test
    @DisplayName("@spec:AC-002 produto válido é desserializado, criado e retorna 201")
    void createsProductFromValidJson() throws Exception {
        UUID productId = UUID.randomUUID();
        when(productService.save(argThat(product ->
                "Notebook".equals(product.getName())
                        && product.getPrice().compareTo(new java.math.BigDecimal("1999.90")) == 0
                        && Integer.valueOf(3).equals(product.getQuantity()))))
                .thenReturn(new Product(productId, "Notebook", new java.math.BigDecimal("1999.90"), 3));

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Notebook\",\"price\":1999.90,\"quantity\":3}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(productId.toString()))
                .andExpect(jsonPath("$.name").value("Notebook"))
                .andExpect(jsonPath("$.price").value(1999.90))
                .andExpect(jsonPath("$.quantity").value(3));
    }

    @Test
    @DisplayName("@spec:AC-003 quantidade ausente ou não positiva retorna 400 sem persistir")
    void rejectsMissingOrNonPositiveQuantityWithoutCallingService() throws Exception {
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Notebook\",\"price\":1999.90}"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Notebook\",\"price\":1999.90,\"quantity\":0}"))
                .andExpect(status().isBadRequest());

        verify(productService, never()).save(org.mockito.ArgumentMatchers.any());
    }
}
