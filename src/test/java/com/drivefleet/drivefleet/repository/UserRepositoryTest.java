package com.drivefleet.drivefleet.repository;

import com.drivefleet.drivefleet.domain.entities.User;
import com.drivefleet.drivefleet.domain.enums.UserRole;
import com.drivefleet.drivefleet.domain.enums.UserStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User createValidUser() {
        return User.builder()
                .name("Test User")
                .email("email@test.com")
                .password("123456")
                .role(UserRole.CUSTOMER)
                .status(UserStatus.ACTIVE)
                .build();
    }

    @Test
    @DisplayName("Should save user successfully")
    void save_PersistUser_WhenSuccessful() {
        User u = createValidUser();
        User saved = userRepository.save(u);

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getEmail()).isEqualTo(u.getEmail());
    }

    @Test
    @DisplayName("Should not save user with null name")
    void save_ThrowsException_WhenNameIsNull() {
        User u = User.builder()
                .email("email@test.com")
                .password("123")
                .role(UserRole.CUSTOMER)
                .build();

        assertThatThrownBy(() -> userRepository.saveAndFlush(u))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("Should not save user with null email")
    void save_ThrowsException_WhenEmailIsNull() {
        User u = User.builder()
                .name("test")
                .password("123")
                .role(UserRole.CUSTOMER)
                .build();

        assertThatThrownBy(() -> userRepository.saveAndFlush(u))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("Should not save user with null password")
    void save_ThrowsException_WhenPasswordIsNull() {
        User u = User.builder()
                .name("test")
                .email("email@test.com")
                .role(UserRole.CUSTOMER)
                .build();

        assertThatThrownBy(() -> userRepository.saveAndFlush(u))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("Should not save user with null role")
    void save_ThrowsException_WhenRoleIsNull() {
        User u = User.builder()
                .name("test")
                .email("email@test.com")
                .password("123")
                .build();

        assertThatThrownBy(() -> userRepository.saveAndFlush(u))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("Should not allow duplicate email if unique")
    void save_ThrowsException_WhenEmailAlreadyExists() {
        User u1 = createValidUser();
        User u2 = createValidUser();

        userRepository.save(u1);

        assertThatThrownBy(() -> userRepository.saveAndFlush(u2))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("findByEmail returns user when exists")
    void findByEmail_ReturnsUser_WhenExists() {
        User u = createValidUser();
        userRepository.save(u);

        Optional<User> result = userRepository.findByEmail(u.getEmail());

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo(u.getEmail());
    }

    @Test
    @DisplayName("findByEmail returns empty when not exists")
    void findByEmail_ReturnsEmpty_WhenNotExists() {
        Optional<User> result = userRepository.findByEmail("nope@test.com");
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("existsByEmail returns true when exists")
    void existsByEmail_ReturnsTrue_WhenExists() {
        User u = createValidUser();
        userRepository.save(u);

        boolean exists = userRepository.existsByEmail(u.getEmail());
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("existsByEmail returns false when not exists")
    void existsByEmail_ReturnsFalse_WhenNotExists() {
        boolean exists = userRepository.existsByEmail("nope@test.com");
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Should update user successfully")
    void updateUser_WhenSuccessful() {
        User u = createValidUser();
        User saved = userRepository.save(u);

        saved.setName("Updated Name");
        User updated = userRepository.save(saved);

        assertThat(updated.getName()).isEqualTo("Updated Name");
        assertThat(updated.getId()).isEqualTo(saved.getId());
    }

    @Test
    @DisplayName("Should delete user successfully")
    void deleteUser_WhenSuccessful() {
        User u = createValidUser();
        User saved = userRepository.save(u);

        userRepository.delete(saved);

        Optional<User> result = userRepository.findById(saved.getId());
        assertThat(result).isEmpty();
    }
}
