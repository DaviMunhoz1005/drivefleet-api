package com.drivefleet.drivefleet.service;

import com.drivefleet.drivefleet.domain.dto.seller.SellerRequest;
import com.drivefleet.drivefleet.domain.dto.user.UserRequest;
import com.drivefleet.drivefleet.domain.dto.seller.SellerResponse;
import com.drivefleet.drivefleet.domain.entities.SalesOrder;
import com.drivefleet.drivefleet.domain.entities.Seller;
import com.drivefleet.drivefleet.domain.entities.User;
import com.drivefleet.drivefleet.domain.enums.UserRole;
import com.drivefleet.drivefleet.domain.enums.UserStatus;
import com.drivefleet.drivefleet.exceptions.SellerCannotBeExcludedException;
import com.drivefleet.drivefleet.exceptions.UserNotFoundIdException;
import com.drivefleet.drivefleet.repository.SalerOrderRepository;
import com.drivefleet.drivefleet.repository.SellerRepository;
import com.drivefleet.drivefleet.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class SellerServiceTest {

    @Autowired
    private SellerRepository sellerRepository;

    @Autowired
    private UserRepository userRepository;

    private SellerService sellerService;
    private UserService userService;
    private SalesOrderService salesOrderService;
    @Mock
    SalerOrderRepository salerOrderRepository;

    @BeforeEach
    void setup() {
        userService = new UserService(userRepository, new BCryptPasswordEncoder());
        salesOrderService = new SalesOrderService(salerOrderRepository);
        sellerService = new SellerService(sellerRepository, userService, salesOrderService);
    }

    private UserRequest validUserRequest() {
        return new UserRequest("Seller", "seller@test.com", "123456", UserRole.SELLER);
    }

    private Seller createSellerEntity() {
        User user = userRepository.save(User.builder()
                .name("Seller")
                .email("seller@test.com")
                .password("123456")
                .role(UserRole.SELLER)
                .status(UserStatus.ACTIVE)
                .build()
        );

        return Seller.builder()
                .registrationNumber(12345678L)
                .user(user)
                .sales(new ArrayList<SalesOrder>())
                .build();
    }

    @Test
    @DisplayName("Should create seller successfully")
    void createSeller_WhenSuccessful() {
        SellerRequest request = new SellerRequest(validUserRequest());
        SellerResponse response = sellerService.create(request);

        assertThat(response).isNotNull();
        assertThat(response.id()).isNotNull();
        assertThat(response.user()).isNotNull();
    }

    @Test
    @DisplayName("Should list only active sellers")
    void listAllActiveSellers_WhenSuccessful() {
        Seller active = sellerRepository.save(createSellerEntity());

        User inactiveUser = userRepository.save(User.builder()
                .name("Inactive")
                .email("inactive@test.com")
                .password("123")
                .role(UserRole.SELLER)
                .status(UserStatus.EXCLUDED)
                .build()
        );

        sellerRepository.save(Seller.builder()
                .registrationNumber(87654321L)
                .user(inactiveUser)
                .build()
        );

        List<SellerResponse> result = sellerService.listAllSellers();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().user().status()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    @DisplayName("Should update seller's user successfully")
    void updateSeller_WhenSuccessful() {
        Seller seller = sellerRepository.save(createSellerEntity());

        SellerRequest updateRequest = new SellerRequest(
                new UserRequest("Updated Seller", "updated@mail.com", "999999", UserRole.SELLER)
        );

        SellerResponse updated = sellerService.update(seller.getId(), updateRequest);

        assertThat(updated.user().email()).isEqualTo("updated@mail.com");
        assertThat(updated.user().name()).isEqualTo("Updated Seller");
    }

    @Test
    @DisplayName("Should NOT allow changing registrationNumber manually")
    void updateSeller_ShouldNotAllowChangingRegistrationNumber() {
        Seller seller = sellerRepository.save(createSellerEntity());

        seller.setRegistrationNumber(99999999L);
        sellerRepository.save(seller);

        SellerRequest req = new SellerRequest(validUserRequest());

        SellerResponse updated = sellerService.update(seller.getId(), req);

        assertThat(updated.registrationNumber()).isEqualTo(99999999L);
    }

    @Test
    @DisplayName("Should throw when updating non-existing seller")
    void updateSeller_ShouldThrow_WhenSellerNotFound() {
        SellerRequest req = new SellerRequest(validUserRequest());

        assertThatThrownBy(() ->
                sellerService.update(UUID.randomUUID(), req)
        ).isInstanceOf(UserNotFoundIdException.class);
    }

    @Test
    @DisplayName("Should delete seller successfully (logical deletion)")
    void deleteSeller_WhenSuccessful() {
        Seller seller = sellerRepository.save(createSellerEntity());

        sellerService.deleteById(seller.getId());

        User deletedUser = userRepository.findById(seller.getUser().getId()).orElseThrow();

        assertThat(deletedUser.getStatus()).isEqualTo(UserStatus.EXCLUDED);
    }

    @Test
    @DisplayName("Should NOT delete seller with sales")
    void deleteSeller_ShouldThrow_WhenHasSales() {
        Seller seller = sellerRepository.save(createSellerEntity());

        seller.getSales().add(new SalesOrder());
        sellerRepository.save(seller);

        assertThatThrownBy(() ->
                sellerService.deleteById(seller.getId())
        ).isInstanceOf(SellerCannotBeExcludedException.class);
    }

    @Test
    @DisplayName("Should throw when deleting non-existing seller")
    void deleteSeller_ShouldThrow_WhenNotFound() {
        assertThatThrownBy(() -> sellerService.deleteById(UUID.randomUUID()))
                .isInstanceOf(UserNotFoundIdException.class);
    }
}
