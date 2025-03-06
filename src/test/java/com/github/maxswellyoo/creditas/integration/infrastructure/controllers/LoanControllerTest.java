package com.github.maxswellyoo.creditas.integration.infrastructure.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.maxswellyoo.creditas.infrastructure.controllers.dto.SimulateLoanRequest;
import com.github.maxswellyoo.creditas.integration.config.TestEmailGatewayConfig;
import com.github.maxswellyoo.creditas.integration.config.TestMailConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.math.BigDecimal;
import java.time.LocalDate;

import static com.github.maxswellyoo.creditas.domain.enums.Currency.USD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Import({TestEmailGatewayConfig.class, TestMailConfig.class})
class LoanControllerTest {
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;


    SimulateLoanRequest request;

    @BeforeEach
    void setUp() {
        request = new SimulateLoanRequest(
                BigDecimal.valueOf(10000),
                LocalDate.of(2004, 2, 11),
                12,
                "test@test.com",
                USD
        );
    }

    @Test
    @DisplayName("Deve simular um empréstimo e retornar status 201 com o DTO esperado")
    void shouldSimulateLoanAndReturnExpectedResponseWhenInputIsValid() throws Exception {
        String requestBody = objectMapper.writeValueAsString(request);
        mockMvc.perform(post("/simulate-loan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .characterEncoding("UTF-8"))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request quando a entrada for inválida")
    void shouldReturnBadRequestWhenInputIsInvalid() throws Exception {
        SimulateLoanRequest invalidRequest = new SimulateLoanRequest(
                BigDecimal.valueOf(-10000),
                LocalDate.now().plusYears(1),
                0,
                "teste",
                USD
        );
        String requestJson = objectMapper.writeValueAsString(invalidRequest);

        mockMvc.perform(post("/simulate-loan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                        .characterEncoding("UTF-8"))
                .andExpect(status().isBadRequest())
                .andDo(MockMvcResultHandlers.print());
    }
}