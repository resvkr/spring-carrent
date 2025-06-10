package com.ebabak.springboot_carrent.service.impl;

import com.ebabak.springboot_carrent.dto.UserRequest;
import com.ebabak.springboot_carrent.model.Role;
import com.ebabak.springboot_carrent.model.User;
import com.ebabak.springboot_carrent.repository.RoleRepository;
import com.ebabak.springboot_carrent.repository.UserRepository;
import com.ebabak.springboot_carrent.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void register(UserRequest req) {
        if (userRepository.findByLogin(req.getLogin()).isPresent()) {
            throw new IllegalArgumentException("Error...");
        }
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() ->
                        new IllegalStateException("There is no role... ROLE_USER"));
        User u = User.builder()
                .id(UUID.randomUUID().toString())
                .login(req.getLogin())
                .password(passwordEncoder.encode(req.getPassword()))
                .roles(Set.of(userRole))
                .build();
        userRepository.save(u);
    }


    @Override
    public Optional<User> findByLogin(String login) {
        return userRepository.findByLogin(login)
                .filter(User::isEnabled);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public void softDeleteUser(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.isEnabled()) {
            throw new RuntimeException("User already deleted");
        }

        user.setEnabled(true);
        userRepository.save(user);
    }

    @Override
    public void assignRole(String id, String roleName) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        user.getRoles().add(role);
        userRepository.save(user);
    }



    @Override
    public void removeRole(String id, String roleName) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean removed = user.getRoles().removeIf(r -> r.getName().equals(roleName));

        if (removed) {
            userRepository.save(user);
        }
    }


}
