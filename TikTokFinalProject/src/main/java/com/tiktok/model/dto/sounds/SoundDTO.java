package com.tiktok.model.dto.sounds;

import com.tiktok.model.entities.User;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SoundDTO {

    private int id;
    private User owner;
    private String title;
    private String soundUrl;
    private LocalDateTime uploadAt;
}
