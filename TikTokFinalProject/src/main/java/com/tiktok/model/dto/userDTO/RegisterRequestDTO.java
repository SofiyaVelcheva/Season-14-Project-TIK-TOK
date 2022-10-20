package com.tiktok.model.dto.userDTO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RegisterRequestDTO {

    private String username;
    private String password;
    private String confirmPassword;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private LocalDateTime dateOfBirth;
    private String profilPhotoURL;

}
