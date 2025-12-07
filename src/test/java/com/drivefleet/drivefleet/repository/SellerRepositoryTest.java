package com.drivefleet.drivefleet.repository;

import com.drivefleet.drivefleet.domain.entities.Seller;
import com.drivefleet.drivefleet.domain.entities.User;
import com.drivefleet.drivefleet.domain.enums.UserRole;
import com.drivefleet.drivefleet.domain.enums.UserStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class SellerRepositoryTest {

    @Autowired
    private SellerRepository sellerRepository;

    @Autowired
    private UserRepository userRepository;

    private User createValidUser(String email) {
        return User.builder()
                .name("Test User")
                .email(email)
                .password("123456")
                .role(UserRole.SELLER)
                .status(UserStatus.ACTIVE)
                .build();
    }

    private Seller createValidSeller(User user) {
        return Seller.builder()
                .registrationNumber(123L)
                .user(user)
                .build();
    }

    @Test
    @DisplayName("Should save seller successfully")
    void save_PersistSeller_WhenSuccessful() {
        User user = userRepository.save(createValidUser("seller@test.com"));
        Seller seller = sellerRepository.save(createValidSeller(user));

        assertThat(seller).isNotNull();
        assertThat(seller.getId()).isNotNull();
        assertThat(seller.getRegistrationNumber()).isEqualTo(123L);
        assertThat(seller.getUser().getEmail()).isEqualTo("seller@test.com");
    }

    @Test
    @DisplayName("Should fail when saving a seller without user")
    void save_ThrowsException_WhenUserIsNull() {
        Seller seller = Seller.builder()
                .registrationNumber(123L)
                .build();

        assertThatThrownBy(() -> sellerRepository.saveAndFlush(seller))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("Should not allow duplicate registration numbers")
    void save_ThrowsException_WhenRegistrationNumberAlreadyExists() {
        User u1 = userRepository.save(createValidUser("a@test.com"));
        User u2 = userRepository.save(createValidUser("b@test.com"));

        Seller s1 = createValidSeller(u1);
        Seller s2 = createValidSeller(u2);

        sellerRepository.save(s1);

        assertThatThrownBy(() -> sellerRepository.saveAndFlush(s2))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("findByRegistrationNumber returns seller when exists")
    void findByRegistrationNumber_ReturnsSeller_WhenExists() {
        User user = userRepository.save(createValidUser("seller@test.com"));
        Seller seller = sellerRepository.save(createValidSeller(user));

        Optional<Seller> result =
                sellerRepository.findByRegistrationNumber(seller.getRegistrationNumber());

        assertThat(result).isPresent();
        assertThat(result.get().getRegistrationNumber()).isEqualTo(123L);
    }

    @Test
    @DisplayName("findByRegistrationNumber returns empty when not exists")
    void findByRegistrationNumber_ReturnsEmpty_WhenNotExists() {
        Optional<Seller> result = sellerRepository.findByRegistrationNumber(999L);
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("existsByRegistrationNumber returns true when exists")
    void existsByRegistrationNumber_ReturnsTrue_WhenExists() {
        User user = userRepository.save(createValidUser("seller@test.com"));
        sellerRepository.save(createValidSeller(user));

        assertThat(sellerRepository.existsByRegistrationNumber(123L)).isTrue();
    }

    @Test
    @DisplayName("existsByRegistrationNumber returns false when not exists")
    void existsByRegistrationNumber_ReturnsFalse_WhenNotExists() {
        assertThat(sellerRepository.existsByRegistrationNumber(999L)).isFalse();
    }

    @Test
    @DisplayName("findAllActive returns only sellers whose users are ACTIVE")
    void findAllActive_ReturnsOnlyActiveSellers() {
        User activeUser = createValidUser("active@test.com");
        activeUser.setStatus(UserStatus.ACTIVE);

        User excludedUser = createValidUser("excluded@test.com");
        excludedUser.setStatus(UserStatus.EXCLUDED);

        userRepository.save(activeUser);
        userRepository.save(excludedUser);

        Seller activeSeller = sellerRepository.save(
                Seller.builder().registrationNumber(1L).user(activeUser).build()
        );

        Seller excludedSeller = sellerRepository.save(
                Seller.builder().registrationNumber(2L).user(excludedUser).build()
        );

        List<Seller> result = sellerRepository.findAllActive(UserStatus.ACTIVE);

        assertThat(result)
                .hasSize(1)
                .contains(activeSeller)
                .doesNotContain(excludedSeller);
    }

    @Test
    @DisplayName("Should delete seller successfully")
    void deleteSeller_WhenSuccessful() {
        User user = userRepository.save(createValidUser("delete@test.com"));
        Seller seller = sellerRepository.save(createValidSeller(user));

        sellerRepository.delete(seller);

        Optional<Seller> result = sellerRepository.findById(seller.getId());
        assertThat(result).isEmpty();
    }
}
