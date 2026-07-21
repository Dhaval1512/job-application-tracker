package com.dhaval.jobtracker.controller;

import com.dhaval.jobtracker.entity.User;
import com.dhaval.jobtracker.repository.UserRepository;
import com.dhaval.jobtracker.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.Instant;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class UserControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    @Autowired
    private JwtService jwtService;

    @MockitoBean
    private UserRepository userRepository;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .addFilter(springSecurityFilterChain)
                .build();
    }

    @Test
    void getCurrentUser_withoutToken_returns401() throws Exception {
        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getCurrentUser_withValidToken_returns200() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setDisplayName("Test User");
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());

        when(userRepository.findByEmailIgnoreCase("test@example.com"))
                .thenReturn(Optional.of(user));

        String token = jwtService.generateToken("test@example.com", 1L);

        mockMvc.perform(get("/api/users/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.displayName").value("Test User"));
    }

    @Test
    void getCurrentUser_withTamperedToken_returns401() throws Exception {
        String validToken = jwtService.generateToken("test@example.com", 1L);
        String tamperedToken = validToken.substring(0, validToken.length() - 5) + "XXXXX";

        mockMvc.perform(get("/api/users/me")
                        .header("Authorization", "Bearer " + tamperedToken))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getCurrentUser_withMalformedHeader_returns401() throws Exception {
        when(userRepository.findByEmailIgnoreCase(anyString()))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/me")
                        .header("Authorization", "NotBearer some.token.here"))
                .andExpect(status().isUnauthorized());
    }
}