package com.tiktok.model.dto.userDTO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BasicUserResponseDTO {

    private long id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String profilPhotoURL;

}
