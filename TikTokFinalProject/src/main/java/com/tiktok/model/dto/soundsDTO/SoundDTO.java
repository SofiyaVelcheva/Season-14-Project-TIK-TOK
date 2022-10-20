package com.tiktok.model.dto.soundsDTO;

import com.tiktok.model.entities.User;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

@Data
public class SoundDTO {
    private int id;
    private User owner;

    private String title;

    private String soundUrl;

    private LocalDateTime uploadAt;
}
