package com.dhaval.jobtracker.controller;

import com.dhaval.jobtracker.entity.User;
import com.dhaval.jobtracker.repository.UserRepository;
import com.dhaval.jobtracker.service.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.security.web.FilterChainProxy;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@WithAnonymousUser
class AuthControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

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
    void register_withValidInput_returns201() throws Exception {
        when(userRepository.existsByEmailIgnoreCase(anyString())).thenReturn(false);
        when(userRepository.save(org.mockito.ArgumentMatchers.any()))
                .thenAnswer(invocation -> {
                    com.dhaval.jobtracker.entity.User u = invocation.getArgument(0);
                    u.setId(1L);
                    u.setCreatedAt(java.time.Instant.now());
                    u.setUpdatedAt(java.time.Instant.now());
                    return u;
                });

        Map<String, String> body = Map.of(
                "email", "new.user@example.com",
                "password", "password123",
                "displayName", "New User"
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("new.user@example.com"))
                .andExpect(jsonPath("$.displayName").value("New User"))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    void register_withInvalidInput_returns400WithFieldErrors() throws Exception {
        Map<String, String> body = Map.of(
                "email", "not-an-email",
                "password", "short",
                "displayName", ""
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("validation failed"))
                .andExpect(jsonPath("$.details.email").exists())
                .andExpect(jsonPath("$.details.password").exists())
                .andExpect(jsonPath("$.details.displayName").exists());
    }

    @Test
    void register_withDuplicateEmail_returns409() throws Exception {
        when(userRepository.existsByEmailIgnoreCase(anyString())).thenReturn(true);

        Map<String, String> body = Map.of(
                "email", "existing@example.com",
                "password", "password123",
                "displayName", "Existing User"
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("email already registered"));
    }

    @Test
    void login_withValidCredentials_returns200WithToken() throws Exception {
        String rawPassword = "password123";
        String hashedPassword = passwordEncoder.encode(rawPassword);

        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setEmail("existing@example.com");
        existingUser.setPasswordHash(hashedPassword);
        existingUser.setDisplayName("Existing User");
        existingUser.setCreatedAt(Instant.now());
        existingUser.setUpdatedAt(Instant.now());

        when(userRepository.findByEmailIgnoreCase("existing@example.com"))
                .thenReturn(Optional.of(existingUser));

        Map<String, String> body = Map.of(
                "email", "existing@example.com",
                "password", rawPassword
        );

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.expiresInMs").isNumber());
    }

    @Test
    void login_withWrongPassword_returns401() throws Exception {
        String correctPassword = "password123";
        String hashedPassword = passwordEncoder.encode(correctPassword);

        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setEmail("existing@example.com");
        existingUser.setPasswordHash(hashedPassword);
        existingUser.setDisplayName("Existing User");

        when(userRepository.findByEmailIgnoreCase("existing@example.com"))
                .thenReturn(Optional.of(existingUser));

        Map<String, String> body = Map.of(
                "email", "existing@example.com",
                "password", "wrongpassword"
        );

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("invalid email or password"));
    }

    @Test
    void login_withNonexistentUser_returns401() throws Exception {
        when(userRepository.findByEmailIgnoreCase("nobody@example.com"))
                .thenReturn(Optional.empty());

        Map<String, String> body = Map.of(
                "email", "nobody@example.com",
                "password", "anypassword"
        );

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("invalid email or password"));
    }
}