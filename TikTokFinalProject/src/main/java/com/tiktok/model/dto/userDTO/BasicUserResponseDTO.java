package com.tiktok.model.dto.userDTO;

import lombok.Data;

@Data
public class BasicUserResponseDTO {

    private long id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String photoURL;

}
