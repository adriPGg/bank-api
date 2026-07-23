package com.adrian.bankapi.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import com.adrian.bankapi.service.TransferService;
import com.adrian.bankapi.service.TransactionService;
import com.adrian.bankapi.security.JwtAuthenticationFilter;
import com.adrian.bankapi.service.CustomUserDetailsService;
import com.adrian.bankapi.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.adrian.bankapi.dto.TransferRequest;
import com.adrian.bankapi.dto.TransferResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransferController.class)
class TransferControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TransferService transferService;

    @MockitoBean
    private TransactionService transactionService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @WithMockUser(username = "test@test.com")
    void transfer_ShouldReturn200() throws Exception {

        TransferResponse response =
                new TransferResponse("Transferencia realizada correctamente");

        when(transferService.transfer(
                any(TransferRequest.class),
                anyString()))
                .thenReturn(response);

        mockMvc.perform(post("/transfers")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "fromAccountId":1,
                      "toAccountId":2,
                      "amount":150
                    }
                    """))
                .andExpect(status().isOk());
    }

}