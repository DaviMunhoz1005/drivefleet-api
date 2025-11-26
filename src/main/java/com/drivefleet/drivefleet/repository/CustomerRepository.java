package com.drivefleet.drivefleet.repository;

import com.drivefleet.drivefleet.domain.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    Optional<Customer> findByCpf(Long cpf);
    Optional<Customer> findByPhone(Long phone);
    boolean existsByCpf(Long cpf);
    boolean existsByPhone(Long phone);
}
