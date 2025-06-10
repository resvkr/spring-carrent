package com.ebabak.springboot_carrent.controller;

import com.ebabak.springboot_carrent.dto.UserRequest;
import com.ebabak.springboot_carrent.model.Role;
import com.ebabak.springboot_carrent.model.User;
import com.ebabak.springboot_carrent.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> softDeleteUser(@PathVariable String id) {
        userService.softDeleteUser(id);
        return ResponseEntity.ok("User soft deleted");
    }

    @PutMapping("/users/{id}/role")
    public ResponseEntity<String> assignRole(@PathVariable String id, @RequestParam String roleName) {
        userService.assignRole(id, roleName);
        return ResponseEntity.ok("Role assigned");
    }


    @PutMapping("/users/{id}/remove-role")
    public ResponseEntity<String> removeRole(@PathVariable String id, @RequestParam String role) {
        userService.removeRole(id, role);
        return ResponseEntity.ok("Role removed");
    }
}
