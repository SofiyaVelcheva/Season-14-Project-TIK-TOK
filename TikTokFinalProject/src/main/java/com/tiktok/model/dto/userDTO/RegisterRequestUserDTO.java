package com.tiktok.model.dto.userDTO;

import lombok.Data;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
public class RegisterRequestUserDTO {

    @NotNull(message = "Empty row")
    @Size(max = 25, message = "invalid username")
    @Pattern(regexp = "^[A-Za-z\\d._]{1,24}$", message = "Invalid username, only contain letters, numbers, underscores, and periods")
    private String username;
    @NotNull(message = "Empty row")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$",
            message = "Password should contain at least one special symbol, at least one digit, \" +\n" +
                    "                    \"at least one capital && at least one small letter.")
    @Size(min = 8, max = 20, message = "password length should be between 8 and 20 symbols")
    private String password;
    @NotNull(message = "Empty row")
    private String confirmPassword;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private LocalDate dateOfBirth;

}
