package com.tiktok.model.dto.userDTO;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class LoginRequestUserDTO {

    @NotBlank(message = "can not be blank")
    @Size(max = 25, message = "invalid")
    private String username;
    @NotBlank(message = "can not be blank")
    @Size(min = 8, max = 20, message = "length should be between 8 and 20 symbols")
    private String password;

}
