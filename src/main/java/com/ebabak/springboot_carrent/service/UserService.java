package com.ebabak.springboot_carrent.service;

import com.ebabak.springboot_carrent.dto.UserRequest;
import com.ebabak.springboot_carrent.model.Role;
import com.ebabak.springboot_carrent.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    void register(UserRequest req);
    Optional<User> findByLogin(String login);
    public List<User> getAllUsers();
    public void softDeleteUser(String id);
    public void assignRole(String id, String roleName);
    public void removeRole(String id, String role);
}
