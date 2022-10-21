package com.tiktok.model.entities;


import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity(name = "sounds")
public class Sound {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @Column
    private String title;

    @Column
    private String soundUrl;

    @Column
    private LocalDateTime uploadAt;
}
