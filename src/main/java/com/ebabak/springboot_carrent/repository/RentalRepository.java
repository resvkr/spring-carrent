package com.ebabak.springboot_carrent.repository;

import com.ebabak.springboot_carrent.model.Rental;
import com.ebabak.springboot_carrent.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface RentalRepository extends JpaRepository<Rental, String> {
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END " +
            "FROM Rental r WHERE r.vehicleId.id = :vehicleId AND r.returnDate IS NULL")
    boolean isVehicleRented(@Param("vehicleId") String vehicleId);

    @Query("SELECT r FROM Rental r WHERE r.vehicleId.id = :vehicleId AND r.returnDate IS NULL")
    Optional<Rental> findActiveRentalByVehicleId(@Param("vehicleId") String vehicleId);
}
