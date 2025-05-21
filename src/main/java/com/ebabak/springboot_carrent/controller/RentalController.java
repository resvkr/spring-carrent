package com.ebabak.springboot_carrent.controller;

import com.ebabak.springboot_carrent.dto.RentalRequest;
import com.ebabak.springboot_carrent.model.Rental;
import com.ebabak.springboot_carrent.model.User;
import com.ebabak.springboot_carrent.repository.UserRepository;
import com.ebabak.springboot_carrent.service.RentalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/rentals")
@RequiredArgsConstructor
public class RentalController {

    private final RentalService rentalService;
    private final UserRepository userRepository;

    @GetMapping
    public List<Rental> getAllRentals() {
        return rentalService.findAll();
    }


    @PostMapping("/rent")
    public ResponseEntity<Rental> rentVehicle(
            @RequestBody RentalRequest rentalRequest,
            @AuthenticationPrincipal UserDetails userDetails) {

        String login = userDetails.getUsername();

        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("Użytkownik nie znaleziony: " + login));

        Rental rental = rentalService.rent(rentalRequest.getVehicleId(), user.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(rental);
    }

    @PostMapping("/return")
    public ResponseEntity<Void> returnVehicle(@RequestParam String vehicleId, @RequestParam String userId) {
        boolean success = rentalService.returnRental(vehicleId, userId);
        return success ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }
}
