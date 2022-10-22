package com.tiktok.model.dto.userDTO;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class ChangePassRequestUserDTO {

    @NotBlank(message = "can not be blank")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$",
            message = "should contain at least one special symbol, at least one digit, at least one capital && at least one small letter.")
    private String currentPassword;
    @NotBlank(message = "can not be blank")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$",
            message = "should contain at least one special symbol, at least one digit, at least one capital && at least one small letter.")
    private String newPassword;
    @NotBlank(message = "can not be blank")
    private String confirmNewPassword;

}
