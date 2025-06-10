package com.ebabak.springboot_carrent.repository;

import com.ebabak.springboot_carrent.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, String> {
    Optional<Role> findByName(String name);
}
