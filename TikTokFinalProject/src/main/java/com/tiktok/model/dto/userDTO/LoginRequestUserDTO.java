package com.tiktok.model.dto.userDTO;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class LoginRequestUserDTO {

    //@NotBlank(message = "username can't be blank")
    //@Size(max = 25, message = "invalid username")
    @NotNull(message = "ERRRRROR")
    private String username;
    //@NotBlank(message = "password can't be blank")
    //@Size(min = 8, max = 20, message = "password length should be between 8 and 20 symbols")
    private String password;

}
