package com.example.demo.domain.user.dto;

import lombok.Data;

@Data
public class UserRegistrationDTO {
    private String name;
    private String email;
    private String role;
}
