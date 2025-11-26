package com.drivefleet.drivefleet.repository;

import com.drivefleet.drivefleet.domain.entities.Seller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SellerRepository extends JpaRepository<Seller, UUID> {
    Optional<Seller> findByRegistrationNumber(Long registrationNumber);
    boolean existsByRegistrationNumber(Long registrationNumber);
}
