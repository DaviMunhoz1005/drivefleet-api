package com.drivefleet.drivefleet.service;

import com.drivefleet.drivefleet.domain.dto.UserRequest;
import com.drivefleet.drivefleet.domain.dto.UserResponse;
import com.drivefleet.drivefleet.domain.entities.User;
import com.drivefleet.drivefleet.exceptions.UserNotFoundException;
import com.drivefleet.drivefleet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponse convertToResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole()
        );
    }

    @Transactional
    public UserResponse create(UserRequest request) {

        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email already in use");
        }

        User newUser = User.builder()
                .name(request.name())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(request.role())
                .build();

        userRepository.save(newUser);
        return convertToResponse(newUser);
    }

    @Transactional
    public void deleteById(UUID id) {

        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id.toString());
        }

        userRepository.deleteById(id);
    }
}

