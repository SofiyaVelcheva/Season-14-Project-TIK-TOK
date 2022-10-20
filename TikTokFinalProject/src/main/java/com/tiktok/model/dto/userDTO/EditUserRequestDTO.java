package com.tiktok.model.dto.userDTO;

import lombok.Data;

@Data
public class EditUserRequestDTO {

    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;

}
