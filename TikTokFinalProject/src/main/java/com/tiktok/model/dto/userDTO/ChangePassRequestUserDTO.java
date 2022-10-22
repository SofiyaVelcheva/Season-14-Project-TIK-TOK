package com.tiktok.model.dto.userDTO;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class ChangePassRequestUserDTO {

    @NotBlank(message = "Current password can't be blank")
    @NotNull(message = "Current password is mandatory")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$",
            message = "Password should contain at least one special symbol, at least one digit, \" +\n" +
                    "                    \"at least one capital && at least one small letter.")
    @Size(min = 8, max = 20, message = "password length should be between 8 and 20 symbols")
    private String currentPassword;
    @NotBlank(message = "New password can't be blank")
    @NotNull(message = "New password is mandatory")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$",
            message = "Password should contain at least one special symbol, at least one digit, \" +\n" +
                    "                    \"at least one capital && at least one small letter.")
    @Size(min = 8, max = 20, message = "password length should be between 8 and 20 symbols")
    private String newPassword;
    @NotBlank(message = "Confirm password can't be blank")
    @NotNull(message = "Confirm password is mandatory")
    private String confirmNewPassword;

}
