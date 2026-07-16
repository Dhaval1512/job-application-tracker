package com.dhaval.jobtracker.service;

import com.dhaval.jobtracker.dto.RegisterRequest;
import com.dhaval.jobtracker.entity.User;
import com.dhaval.jobtracker.exception.EmailAlreadyExistsException;
import com.dhaval.jobtracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User register(RegisterRequest request) {
        String email = request.email().trim();

        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new EmailAlreadyExistsException(email);
        }

        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setDisplayName(request.displayName().trim());

        return userRepository.save(user);
    }
}