package com.appsdeveloperblog.ccps.web.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CreditCardProcessorController.class)
class CreditCardProcessorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("@spec:AC-010 processador aceita pagamento válido e rejeita valores ausentes ou não positivos")
    void validatesCreditCardProcessingInput() throws Exception {
        mockMvc.perform(post("/ccp/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"creditCardNumber\":\"4111111111111111\",\"paymentAmount\":10.50}"))
                .andExpect(status().isAccepted());

        mockMvc.perform(post("/ccp/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"paymentAmount\":10.50}"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/ccp/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"creditCardNumber\":\"4111111111111111\",\"paymentAmount\":0}"))
                .andExpect(status().isBadRequest());
    }
}
