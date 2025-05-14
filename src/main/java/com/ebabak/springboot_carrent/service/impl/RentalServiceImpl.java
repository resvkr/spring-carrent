package com.ebabak.springboot_carrent.service.impl;

import com.ebabak.springboot_carrent.model.Rental;
import com.ebabak.springboot_carrent.model.User;
import com.ebabak.springboot_carrent.model.Vehicle;
import com.ebabak.springboot_carrent.repository.RentalRepository;
import com.ebabak.springboot_carrent.repository.UserRepository;
import com.ebabak.springboot_carrent.repository.VehicleRepository;
import com.ebabak.springboot_carrent.service.RentalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RentalServiceImpl implements RentalService {

    private final RentalRepository rentalRepository;
    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;

    @Override
    public boolean isVehicleRented(String vehicleId) {
        return rentalRepository.isVehicleRented(vehicleId);
    }

    @Override
    public Optional<Rental> findActiveRentalByVehicleId(String vehicleId) {
        return rentalRepository.findActiveRentalByVehicleId(vehicleId);
    }

    @Override
    public Rental rent(String vehicleId, String userId) {
        if (isVehicleRented(vehicleId)) {
            throw new IllegalStateException("Vehicle is already rented");
        }

        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Rental rental = Rental.builder()
                .id(UUID.randomUUID().toString())
                .vehicleId(vehicle)
                .user(user)
                .rentDate(LocalDate.now().toString())
                .build();

        return rentalRepository.save(rental);
    }

    @Override
    public boolean returnRental(String vehicleId, String userId) {
        Optional<Rental> rentalOpt = rentalRepository.findActiveRentalByVehicleId(vehicleId);

        if (rentalOpt.isPresent()) {
            Rental rental = rentalOpt.get();
            if (!rental.getUser().getId().equals(userId)) {
                throw new IllegalStateException("User is not the renter of this vehicle");
            }

            rental.setReturnDate(LocalDate.now().toString());
            rentalRepository.save(rental);
            return true;
        }

        return false;
    }

    @Override
    public List<Rental> findAll() {
        return rentalRepository.findAll();
    }
}
