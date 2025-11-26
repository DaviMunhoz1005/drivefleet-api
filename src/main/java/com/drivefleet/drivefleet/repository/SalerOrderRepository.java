package com.drivefleet.drivefleet.repository;

import com.drivefleet.drivefleet.domain.entities.SalesOrder;
import com.drivefleet.drivefleet.domain.entities.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SalerOrderRepository extends JpaRepository<SalesOrder, UUID> {
    Optional<SalesOrder> findByVehicle(Vehicle vehicle);
    boolean existsByVehicle(Vehicle vehicle);
}
