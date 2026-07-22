package com.adrian.bankapi.controller;

import com.adrian.bankapi.dto.BankAccountRequest;
import com.adrian.bankapi.entity.AccountType;

import com.adrian.bankapi.security.JwtAuthenticationFilter;
import com.adrian.bankapi.service.BankAccountService;

import com.adrian.bankapi.service.CustomUserDetailsService;
import com.adrian.bankapi.service.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Disabled;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@Disabled("Pendiente de configurar Security en tests")

@WebMvcTest(
        controllers = BankAccountController.class,
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = com.adrian.bankapi.config.SecurityConfig.class
                ),
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = com.adrian.bankapi.security.JwtAuthenticationFilter.class
                )
        }
)
@AutoConfigureMockMvc(addFilters = false)
class BankAccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BankAccountService bankAccountService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createAccount_ShouldReturn200() throws Exception {

        BankAccountRequest request = new BankAccountRequest();
        request.setAccountType(AccountType.CHECKING);

        mockMvc.perform(post("/accounts")
                        .with(user("adrian@test.com"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}