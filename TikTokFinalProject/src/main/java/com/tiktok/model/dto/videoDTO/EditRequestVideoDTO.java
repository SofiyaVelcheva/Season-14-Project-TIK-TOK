package com.tiktok.model.dto.videoDTO;

import lombok.Data;

@Data
public class EditRequestVideoDTO {

    private boolean isPrivate;
    private String description;

}
