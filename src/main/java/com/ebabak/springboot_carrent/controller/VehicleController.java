package com.ebabak.springboot_carrent.controller;

import com.ebabak.springboot_carrent.model.Vehicle;
import com.ebabak.springboot_carrent.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;

    @Autowired
    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @GetMapping
    public List<Vehicle> getAllVehicles() {
        return vehicleService.findAll();
    }

    @GetMapping("/active")
    public List<Vehicle> getActiveVehicles() {
        return vehicleService.findAllActive();
    }

    @GetMapping("/available")
    public List<Vehicle> getAvailableVehicles() {
        return vehicleService.findAvailableVehicles();
    }

    @GetMapping("/rented")
    public List<Vehicle> getRentedVehicles() {
        return vehicleService.findRentedVehicles();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Vehicle> getVehicleById(@PathVariable String id) {
        return vehicleService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Vehicle> addVehicle(@RequestBody Vehicle vehicle) {
        return ResponseEntity.ok(vehicleService.save(vehicle));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> softDeleteVehicle(@PathVariable String id) {
        vehicleService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/availability")
    public ResponseEntity<Boolean> isAvailable(@PathVariable String id) {
        return ResponseEntity.ok(vehicleService.isAvailable(id));
    }

}