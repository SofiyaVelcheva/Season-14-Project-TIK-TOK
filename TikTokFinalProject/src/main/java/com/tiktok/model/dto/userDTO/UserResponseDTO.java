package com.tiktok.model.dto.userDTO;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UserResponseDTO {

    private int id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private String photoURL;
    private int numberOfVideos;
    private int numberOfSubscribers;
    private int numberOfSubscribeTo;
    // todo numberOfLikes

}
