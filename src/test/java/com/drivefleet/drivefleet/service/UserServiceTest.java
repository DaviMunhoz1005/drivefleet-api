package com.drivefleet.drivefleet.service;

import com.drivefleet.drivefleet.domain.dto.UserRequest;
import com.drivefleet.drivefleet.domain.dto.UserResponse;
import com.drivefleet.drivefleet.domain.entities.User;
import com.drivefleet.drivefleet.domain.enums.UserRole;
import com.drivefleet.drivefleet.exceptions.EmailAlreadyInUseException;
import com.drivefleet.drivefleet.exceptions.UserNotFoundEmailException;
import com.drivefleet.drivefleet.exceptions.UserNotFoundIdException;
import com.drivefleet.drivefleet.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    UserService userService;

    @Test
    @DisplayName("create() should create a user when email is not in use")
    void create_ShouldCreate_WhenEmailNotUsed() {
        UserRequest req = new UserRequest("John", "john@test.com", "123", UserRole.CUSTOMER);

        when(userRepository.existsByEmail("john@test.com")).thenReturn(false);
        when(passwordEncoder.encode("123")).thenReturn("encoded");

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

        UserResponse result = userService.create(req);

        verify(userRepository).save(captor.capture());
        User saved = captor.getValue();

        assertThat(saved.getName()).isEqualTo("John");
        assertThat(saved.getEmail()).isEqualTo("john@test.com");
        assertThat(saved.getPassword()).isEqualTo("encoded");

        assertThat(result.email()).isEqualTo("john@test.com");
    }

    @Test
    @DisplayName("create() should throw exception when email already in use")
    void create_ShouldThrow_WhenEmailInUse() {
        UserRequest req = new UserRequest("John", "john@test.com", "123", UserRole.CUSTOMER);

        when(userRepository.existsByEmail("john@test.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.create(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email already in use");

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("update() should updates user when id exists and email does not change")
    void update_ShouldUpdate_WhenEmailSame() {
        UUID id = UUID.randomUUID();

        User user = User.builder()
                .id(id)
                .name("Old")
                .email("old@test.com")
                .password("oldpass")
                .role(UserRole.CUSTOMER)
                .build();

        UserRequest req = new UserRequest("New", "old@test.com", "999", UserRole.CUSTOMER);

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("999")).thenReturn("encodedPass");

        UserResponse result = userService.update(id, req);

        assertThat(user.getName()).isEqualTo("New");
        assertThat(user.getPassword()).isEqualTo("encodedPass");

        assertThat(result.name()).isEqualTo("New");
    }

    @Test
    @DisplayName("update() should updates email when a new email is available")
    void update_ShouldUpdateEmail_WhenEmailAvailable() {
        UUID id = UUID.randomUUID();

        User user = User.builder()
                .id(id)
                .name("Old")
                .email("old@test.com")
                .password("p")
                .role(UserRole.CUSTOMER)
                .build();

        UserRequest req = new UserRequest("New", "new@test.com", "999", UserRole.CUSTOMER);

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail("new@test.com")).thenReturn(false);
        when(passwordEncoder.encode("999")).thenReturn("encodedPass");

        UserResponse result = userService.update(id, req);

        assertThat(user.getEmail()).isEqualTo("new@test.com");
        assertThat(result.email()).isEqualTo("new@test.com");
    }

    @Test
    @DisplayName("update() should throw exception when a new email is already in use")
    void update_ShouldThrow_WhenNewEmailExists() {
        UUID id = UUID.randomUUID();

        User user = User.builder()
                .id(id)
                .name("Old")
                .email("old@test.com")
                .password("p")
                .role(UserRole.CUSTOMER)
                .build();

        UserRequest req = new UserRequest("New", "existing@test.com", "999", UserRole.CUSTOMER);

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail("existing@test.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.update(id, req))
                .isInstanceOf(EmailAlreadyInUseException.class);

        assertThat(user.getEmail()).isEqualTo("old@test.com");
    }

    @Test
    @DisplayName("update() should not update user role when role changes")
    void update_ShouldNotUpdate_WhenRoleChange() {
        UUID id = UUID.randomUUID();

        User user = User.builder()
                .id(id)
                .name("Old")
                .email("old@test.com")
                .password("oldpass")
                .role(UserRole.CUSTOMER)
                .build();

        UserRequest req = new UserRequest("New", "old@test.com", "oldpass", UserRole.SELLER);

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("oldpass")).thenReturn("encodedPass");

        UserResponse result = userService.update(id, req);

        assertThat(user.getName()).isEqualTo("New");
        assertThat(user.getPassword()).isEqualTo("encodedPass");
        assertThat(result.role()).isEqualTo(UserRole.CUSTOMER);
        assertThat(result.name()).isEqualTo("New");
    }

    @Test
    @DisplayName("update() should throw exception when id does not exists")
    void update_ShouldThrow_WhenIdNotFound() {
        UUID id = UUID.randomUUID();

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        UserRequest req = new UserRequest("x", "y@test.com", "p", UserRole.CUSTOMER);

        assertThatThrownBy(() -> userService.update(id, req))
                .isInstanceOf(UserNotFoundIdException.class);
    }

    @Test
    @DisplayName("deleteById() should delete when id exists")
    void delete_ShouldDelete_WhenExists() {
        UUID id = UUID.randomUUID();

        when(userRepository.existsById(id)).thenReturn(true);

        userService.deleteById(id);

        verify(userRepository).deleteById(id);
    }

    @Test
    @DisplayName("deleteById() should throw exception when id does not exists")
    void delete_ShouldThrow_WhenIdNotFound() {
        UUID id = UUID.randomUUID();

        when(userRepository.existsById(id)).thenReturn(false);

        assertThatThrownBy(() -> userService.deleteById(id))
                .isInstanceOf(UserNotFoundIdException.class);

        verify(userRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("findById() should return a user when exists")
    void findById_ShouldReturn_WhenExists() {
        UUID id = UUID.randomUUID();

        User user = User.builder()
                .id(id).name("A").email("a@test.com").role(UserRole.CUSTOMER)
                .build();

        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        UserResponse result = userService.findById(id);

        assertThat(result.id()).isEqualTo(id);
    }

    @Test
    @DisplayName("findById() should throw exception when does not exist")
    void findById_ShouldThrow_WhenNotExists() {
        UUID id = UUID.randomUUID();

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findById(id))
                .isInstanceOf(UserNotFoundIdException.class);
    }

    @Test
    @DisplayName("findByEmail() should return a user when exists")
    void findByEmail_ShouldReturn_WhenExists() {
        User user = User.builder()
                .id(UUID.randomUUID())
                .name("A")
                .email("a@test.com")
                .build();

        when(userRepository.findByEmail("a@test.com")).thenReturn(Optional.of(user));

        UserResponse result = userService.findByEmail("a@test.com");

        assertThat(result.email()).isEqualTo("a@test.com");
    }

    @Test
    @DisplayName("findByEmail() should throw exception when does not exist")
    void findByEmail_ShouldThrow_WhenNotExists() {
        when(userRepository.findByEmail("x@test.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findByEmail("x@test.com"))
                .isInstanceOf(UserNotFoundEmailException.class);
    }
}