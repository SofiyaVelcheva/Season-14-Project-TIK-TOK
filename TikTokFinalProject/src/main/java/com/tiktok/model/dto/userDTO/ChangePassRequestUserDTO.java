package com.tiktok.model.dto.userDTO;

import lombok.Data;

@Data
public class ChangePassRequestUserDTO {

    private String currentPassword;
    private String newPassword;
    private String confirmNewPassword;

}
