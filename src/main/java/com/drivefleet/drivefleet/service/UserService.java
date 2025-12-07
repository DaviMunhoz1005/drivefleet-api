package com.drivefleet.drivefleet.service;

import com.drivefleet.drivefleet.domain.dto.user.UserRequest;
import com.drivefleet.drivefleet.domain.dto.user.UserResponse;
import com.drivefleet.drivefleet.domain.entities.User;
import com.drivefleet.drivefleet.exceptions.EmailAlreadyInUseException;
import com.drivefleet.drivefleet.exceptions.UserNotFoundEmailException;
import com.drivefleet.drivefleet.exceptions.UserNotFoundIdException;
import com.drivefleet.drivefleet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    protected UserResponse convertToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getName())
                .name(user.getEmail())
                .role(user.getRole())
                .build();
    }

    @Transactional
    protected User create(UserRequest request) {
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
        return newUser;
    }

    @Transactional
    protected UserResponse update(UUID id, UserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundIdException(id.toString()));

        user.setName(request.name());

        if (!user.getEmail().equals(request.email()) &&
                userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyInUseException(request.email());
        }
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));

        return convertToResponse(user);
    }

    @Transactional
    protected void deleteById(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundIdException(id.toString());
        }

        userRepository.deleteById(id);
    }

    protected UserResponse findById(UUID id) {
        return convertToResponse(userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundIdException(id.toString())));
    }

    protected UserResponse findByEmail(String email) {
        return convertToResponse(userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundEmailException(email)));
    }
}

