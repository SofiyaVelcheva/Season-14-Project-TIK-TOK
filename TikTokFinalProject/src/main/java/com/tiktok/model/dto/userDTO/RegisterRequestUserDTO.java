package com.tiktok.model.dto.userDTO;

import lombok.Data;

import java.time.LocalDate;

@Data
public class RegisterRequestUserDTO {

    private String username;
    private String password;

    private String confirmPassword;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private LocalDate dateOfBirth;

}
