package com.tiktok.model.dto.userDTO;

import lombok.Data;

import javax.persistence.Column;
import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
public class RegisterRequestUserDTO {

    @NotBlank(message = "can not be blank")
    @Pattern(regexp = "^[A-Za-z\\d._]{1,24}$", message = "Invalid username, only contain letters, numbers, underscores, and periods")
    private String username;
    @NotBlank(message = "can not be blank")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$",
            message = "should contain at least one special symbol, at least one digit, at least one capital && at least one small letter.")
    private String password;
    @NotBlank(message = "can not be blank")
    private String confirmPassword;
    @NotBlank(message = "can't be blank")
    @Pattern(regexp = "^[a-zA-Z]{1,50}$", message = "length should be maximum 50 symbols and should contain only letters.")
    private String firstName;
    @NotBlank(message = "can not be blank")
    @Pattern(regexp = "^[a-zA-Z]{1,50}$", message = "length should be maximum 50 symbols and should contain only letters.")
    private String lastName;
    @NotBlank(message = "can not be blank")
    @Size(min = 1, max = 50, message = "length should be maximum 50 symbols")
    @Pattern(regexp = "^\\S+@\\S+\\.\\S+$", message = "invalid")
    private String email;
    @NotBlank(message = "can not be blank")
    @Pattern(regexp = "^([+]3598)[7-9][0-9]{7}$", message = "invalid")
    private String phoneNumber;
    @NotNull(message = "can not be null")
    @Past(message = "invalid date")
    private LocalDate dateOfBirth;

}
