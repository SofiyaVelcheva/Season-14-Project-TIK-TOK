package com.tiktok.model.dto.userDTO;

import lombok.Data;

@Data
public class ChangePassRequestDTO {

    private String currentPassword;
    private String newPassword;
    private String confirmNewPassword;

}
