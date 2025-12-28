package com.example.demo.domain.user.event;

import com.example.demo.domain.user.entity.User;
import com.example.demo.domain.shared.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class UserRegistrationListener {

    private final EmailService emailService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleUserRegisteredEvent(UserRegisteredEvent event) {
        User user = event.getUser();
        System.out.println("Transaction committed. Sending activation email to " + user.getEmail());

        emailService.sendSimpleMessage(
                user.getEmail(),
                "Activate your account",
                "Hello " + user.getName() + ", please activate your account.");
    }
}
