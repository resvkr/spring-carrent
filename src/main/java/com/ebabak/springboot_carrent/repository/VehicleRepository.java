package com.ebabak.springboot_carrent.repository;

import com.ebabak.springboot_carrent.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, String> {


    List<Vehicle> findByDeletedFalse();

    @Query("SELECT v FROM Vehicle v WHERE v.deleted = false AND v.id NOT IN " +
            "(SELECT r.vehicleId.id FROM Rental r WHERE r.returnDate IS NULL)")
    List<Vehicle> findAvailableVehicles();

    @Query("SELECT v FROM Vehicle v WHERE v.deleted = false AND v.id IN " +
            "(SELECT r.vehicleId.id FROM Rental r WHERE r.returnDate IS NULL)")
    List<Vehicle> findRentedVehicles();

}
