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

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column (name = "video_url")
    private String videoUrl;

    @Column
    private LocalDateTime uploadAt;

    @Column
    private boolean isLive;

    @Column
    private boolean isPrivate;

    @OneToOne
    @JoinColumn(name = "sound_id")
    private Sound sound;

    @Column
    private String description;

}
