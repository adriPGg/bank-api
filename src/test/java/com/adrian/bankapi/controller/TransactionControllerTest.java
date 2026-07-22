package com.adrian.bankapi.controller;

import com.adrian.bankapi.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.adrian.bankapi.dto.TransactionResponse;
import com.adrian.bankapi.entity.TransactionType;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import org.springframework.data.domain.Page;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.adrian.bankapi.security.JwtAuthenticationFilter;
import com.adrian.bankapi.service.CustomUserDetailsService;
import com.adrian.bankapi.service.JwtService;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest
@AutoConfigureMockMvc
class TransactionControllerTest {

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TransactionService transactionService;

    @Test
    @WithMockUser(username = "test@test.com")
    void getTransactions_ShouldReturn200() throws Exception {

        when(transactionService.getMyTransactions(
                anyString(),
                any(),
                anyInt(),
                anyInt()))
                .thenReturn(Page.empty());

        mockMvc.perform(get("/transactions"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "test@test.com")
    void getTransactions_ShouldAcceptPaginationParameters() throws Exception {

        when(transactionService.getMyTransactions(
                anyString(),
                any(),
                anyInt(),
                anyInt()))
                .thenReturn(Page.empty());

        mockMvc.perform(
                        get("/transactions")
                                .param("page", "2")
                                .param("size", "5")
                )
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "test@test.com")
    void getTransactions_ShouldReturnTransactionData() throws Exception {

        TransactionResponse response = new TransactionResponse();
        response.setId(1L);
        response.setAmount(BigDecimal.valueOf(150));
        response.setTransactionType(TransactionType.TRANSFER);

        when(transactionService.getMyTransactions(
                anyString(),
                any(),
                anyInt(),
                anyInt()))
                .thenReturn(new PageImpl<>(List.of(response)));

        mockMvc.perform(get("/transactions"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].amount").value(150))
                .andExpect(jsonPath("$.content[0].transactionType").value("TRANSFER"));
    }
}