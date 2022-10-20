package com.tiktok.model.dto.videoDTO;

import com.tiktok.model.entities.Sound;
import lombok.Data;

@Data
public class EditResponseVideoDTO {

    private boolean isPrivate;
    private Sound sound;
    private String description;

}
