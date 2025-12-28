package com.example.demo.domain.user.service;

import com.example.demo.domain.user.dto.UserRegistrationDTO;
import com.example.demo.domain.user.entity.User;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> getAllUsers();

    Optional<User> getUserById(Long id);

    User createUser(User user);

    User registerUser(UserRegistrationDTO registrationDTO);

    User updateUser(Long id, User userDetails);

    void deleteUser(Long id);

    ByteArrayInputStream getPdfReport();
}
