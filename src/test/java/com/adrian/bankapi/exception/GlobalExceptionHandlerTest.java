package com.adrian.bankapi.exception;

import com.adrian.bankapi.security.JwtAuthenticationFilter;
import com.adrian.bankapi.service.CustomUserDetailsService;
import com.adrian.bankapi.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GlobalExceptionHandlerTest.TestController.class)
@Import(GlobalExceptionHandler.class)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @RestController
    static class TestController {

        @GetMapping("/user-not-found")
        public ResponseEntity<Void> userNotFound() {
            throw new UserNotFoundException("Usuario no encontrado");
        }
    }

    @Test
    @WithMockUser(username = "test@test.com")
    void shouldReturn404_WhenUserNotFoundExceptionIsThrown() throws Exception {

        mockMvc.perform(get("/user-not-found"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error")
                        .value("Usuario no encontrado"));
    }
}