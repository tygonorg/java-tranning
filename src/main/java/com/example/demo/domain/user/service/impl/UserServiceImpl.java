package com.example.demo.domain.user.service.impl;

import com.example.demo.domain.user.dto.UserRegistrationDTO;
import com.example.demo.domain.user.entity.User;
import com.example.demo.domain.user.event.UserRegisteredEvent;
import com.example.demo.domain.user.repository.UserRepository;
import com.example.demo.domain.user.service.UserService;
import com.example.demo.domain.shared.PdfService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PdfService pdfService;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public User registerUser(UserRegistrationDTO registrationDTO) {
        User user = new User();
        user.setName(registrationDTO.getName());
        user.setEmail(registrationDTO.getEmail());
        user.setRole(registrationDTO.getRole());

        User savedUser = userRepository.save(user);

        eventPublisher.publishEvent(new UserRegisteredEvent(this, savedUser));

        return savedUser;
    }

    @Override
    public User updateUser(Long id, User userDetails) {
        return userRepository.findById(id).map(user -> {
            user.setName(userDetails.getName());
            user.setEmail(userDetails.getEmail());
            user.setRole(userDetails.getRole());
            return userRepository.save(user);
        }).orElseThrow(() -> new com.example.demo.domain.shared.exception.ResourceNotFoundException(
                "User not found with id: " + id));
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public ByteArrayInputStream getPdfReport() {
        List<User> users = getAllUsers();
        return pdfService.userReport(users);
    }
}
