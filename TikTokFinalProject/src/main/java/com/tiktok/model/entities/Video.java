package com.tiktok.model.entities;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "videos")
public class Video {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column
    private User user;
    @Column
    private String videoURL;
    @Column
    private LocalDateTime uploadAt;
    @Column
    private boolean isLive;
    @Column
    private boolean isPrivate;
    @Column
    private Sound sound;
    @Column
    private String description;

}
