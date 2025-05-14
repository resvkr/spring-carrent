package com.ebabak.springboot_carrent.service.impl;

import com.ebabak.springboot_carrent.model.Vehicle;
import com.ebabak.springboot_carrent.repository.VehicleRepository;
import com.ebabak.springboot_carrent.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;

    @Override
    public List<Vehicle> findAll() {
        return vehicleRepository.findAll();
    }

    @Override
    public List<Vehicle> findAllActive() {
        return vehicleRepository.findByDeletedFalse();
    }

    @Override
    public Optional<Vehicle> findById(String id) {
        return vehicleRepository.findById(id);
    }

    @Override
    public Vehicle save(Vehicle vehicle) {
        if (vehicle.getId() == null || vehicle.getId().isEmpty()) {
            vehicle.setId(UUID.randomUUID().toString());
        }
        return vehicleRepository.save(vehicle);
    }

    @Override
    public List<Vehicle> findAvailableVehicles() {
        return vehicleRepository.findAvailableVehicles();
    }

    @Override
    public List<Vehicle> findRentedVehicles() {
        return vehicleRepository.findRentedVehicles();
    }

    @Override
    public boolean isAvailable(String vehicleId) {
        return vehicleRepository.findAvailableVehicles().stream()
                .anyMatch(vehicle -> vehicle.getId().equals(vehicleId));
    }

    @Override
    public void deleteById(String id) {
        vehicleRepository.findById(id).ifPresent(vehicle -> {
            vehicle.setDeleted(true);
            vehicleRepository.save(vehicle);
        });
    }

}
