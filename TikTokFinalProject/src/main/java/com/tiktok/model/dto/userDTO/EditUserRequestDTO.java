package com.tiktok.model.dto.userDTO;

import lombok.Data;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class EditUserRequestDTO {

    @Pattern(regexp = "^[a-zA-Z]{1,50}$", message = "length should be maximum 50 symbols and should contain only letters.")
    private String firstName;
    @Pattern(regexp = "^[a-zA-Z]{1,50}$", message = "length should be maximum 50 symbols and should contain only letters.")
    private String lastName;
    @Size(min = 1, max = 50, message = "length should be maximum 50 symbols")
    @Pattern(regexp = "^\\S+@\\S+\\.\\S+$", message = "invalid")
    private String email;
    @Pattern(regexp = "^([+]3598)[7-9][0-9]{7}$", message = "invalid")
    private String phoneNumber;

}
