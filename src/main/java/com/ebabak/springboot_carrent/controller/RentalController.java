package com.ebabak.springboot_carrent.controller;

import com.ebabak.springboot_carrent.model.Rental;
import com.ebabak.springboot_carrent.service.RentalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/rentals")
@RequiredArgsConstructor
public class RentalController {

    private final RentalService rentalService;

    @GetMapping
    public List<Rental> getAllRentals() {
        return rentalService.findAll();
    }

    @GetMapping("/active/{vehicleId}")
    public ResponseEntity<Rental> getActiveRental(@PathVariable String vehicleId) {
        return rentalService.findActiveRentalByVehicleId(vehicleId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/rent")
    public ResponseEntity<Rental> rentVehicle(@RequestParam String vehicleId, @RequestParam String userId) {
        try {
            Rental rental = rentalService.rent(vehicleId, userId);
            return ResponseEntity.ok(rental);
        } catch (IllegalStateException | NoSuchElementException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/return")
    public ResponseEntity<Void> returnVehicle(@RequestParam String vehicleId, @RequestParam String userId) {
        boolean success = rentalService.returnRental(vehicleId, userId);
        return success ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }
}
