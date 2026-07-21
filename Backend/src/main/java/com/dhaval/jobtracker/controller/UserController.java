package com.dhaval.jobtracker.controller;

import com.dhaval.jobtracker.dto.UserResponse;
import com.dhaval.jobtracker.entity.User;
import com.dhaval.jobtracker.exception.InvalidCredentialsException;
import com.dhaval.jobtracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(InvalidCredentialsException::new);
        return ResponseEntity.ok(UserResponse.from(user));
    }
}