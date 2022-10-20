package com.tiktok.model.dto.videoDTO;

import lombok.Data;

@Data
public class EditRequestDTO {

    private int id;
    private boolean isPrivate;
    private String description;

}
