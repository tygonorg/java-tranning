package com.example.demo.domain.user.service;

import com.example.demo.domain.user.dto.UserRegistrationDTO;
import com.example.demo.domain.user.repository.UserRepository;
import com.example.demo.domain.shared.EmailService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@org.springframework.context.annotation.Import(UserServiceIntegrationTest.RollbackTriggerService.class)
public class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private EmailService emailService;

    @Autowired
    private RollbackTriggerService rollbackTriggerService;

    @Test
    public void testRegisterUser_Success() {
        // Arrange
        UserRegistrationDTO dto = new UserRegistrationDTO();
        dto.setName("Test User");
        dto.setEmail("test@example.com");
        dto.setRole("USER");

        // Act
        userService.registerUser(dto);

        // Assert
        // 1. Verify user is saved in DB
        Assertions.assertTrue(userRepository.findAll().stream()
                .anyMatch(u -> u.getEmail().equals("test@example.com")));

        // 2. Verify email service was called (Transactional Event Listener should fire)
        // Need to wait a bit as listeners can be async depending on config, but
        // TransactionalEventListener usually runs in phase.
        // However, standard TestExecutionListeners might rollback the test transaction
        // itself!
        // If the test method is @Transactional, checking "AFTER_COMMIT" is tricky
        // because the test transaction never commits (it rolls back by default).

        // Strategy: We are NOT annotating this test method with @Transactional.
        // This means the service method call creates its own transaction and commits
        // it.
        // So UserRegistrationListener should fire.

        verify(emailService, times(1)).sendSimpleMessage(
                Mockito.eq("test@example.com"),
                anyString(),
                anyString());
    }

    @Test
    public void testRegisterUser_Rollback_NoEmail() {
        // Arrange
        UserRegistrationDTO dto = new UserRegistrationDTO();
        dto.setName("Rollback User");
        dto.setEmail("rollback@example.com");
        dto.setRole("USER");

        // Act
        try {
            rollbackTriggerService.registerAndRollback(dto);
        } catch (RuntimeException e) {
            // Expected
        }

        // Assert
        // 1. Verify user is NOT saved in DB
        Assertions.assertFalse(userRepository.findAll().stream()
                .anyMatch(u -> u.getEmail().equals("rollback@example.com")));

        // 2. Verify email service was NOT called
        verify(emailService, times(0)).sendSimpleMessage(
                anyString(),
                anyString(),
                anyString());
    }

    // Helper service to trigger rollback
    @Service
    public static class RollbackTriggerService {

        @Autowired
        private UserService userService;

        @Transactional
        public void registerAndRollback(UserRegistrationDTO dto) {
            userService.registerUser(dto);
            throw new RuntimeException("Forcing rollback for testing");
        }
    }
}
