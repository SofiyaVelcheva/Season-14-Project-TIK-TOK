package com.tiktok.model.dto.messageDTO;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class SendMessageRequestDTO {

    @NotBlank(message = "can not be blank")
    @Size(min = 1, max = 500, message = " length should be maximum 500 symbols.")
    private String text;

}
