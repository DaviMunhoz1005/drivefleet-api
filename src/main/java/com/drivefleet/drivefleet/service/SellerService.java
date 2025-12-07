package com.drivefleet.drivefleet.service;

import com.drivefleet.drivefleet.domain.dto.seller.SellerRequest;
import com.drivefleet.drivefleet.domain.dto.seller.SellerResponse;
import com.drivefleet.drivefleet.domain.entities.Seller;
import com.drivefleet.drivefleet.domain.entities.User;
import com.drivefleet.drivefleet.domain.enums.UserStatus;
import com.drivefleet.drivefleet.exceptions.SellerCannotBeExcludedException;
import com.drivefleet.drivefleet.exceptions.UserNotFoundIdException;
import com.drivefleet.drivefleet.repository.SellerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class SellerService {

    private final SellerRepository sellerRepository;
    private final UserService userService;
    private final SalesOrderService salesOrderService;

    public SellerResponse create(SellerRequest request) {
        User user = userService.create(request.user());
        Seller seller = Seller.builder()
                .registrationNumber(generateRegistrationNumber())
                .user(user)
                .sales(new ArrayList<>())
                .build();
        sellerRepository.save(seller);
        return convertToResponse(seller);
    }

    @Transactional
    public void deleteById(UUID id) {
        Seller seller = sellerRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundIdException(id.toString()));
        if (!seller.getSales().isEmpty()) {
            throw new SellerCannotBeExcludedException(id.toString());
        }
        seller.getUser().setStatus(UserStatus.EXCLUDED);
    }

    private SellerResponse convertToResponse(Seller seller) {
        return SellerResponse.builder()
                .id(seller.getId())
                .registrationNumber(seller.getRegistrationNumber())
                .user(userService.convertToResponse(seller.getUser()))
                .sales(seller.getSales()
                        .stream()
                        .map(salesOrderService::convertToResponse)
                        .toList()
                )
                .build();
    }

    private Long generateRegistrationNumber() {
        Long number;
        do {
            number = ThreadLocalRandom.current()
                    .nextLong(10_000_000L, 99_999_999L);
        } while (sellerRepository.existsByRegistrationNumber(number));
        return number;
    }
}
