package com.tiktok.model.entities;


import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "sounds")
@Setter
@Getter
@NoArgsConstructor
public class Sound {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column
    private String title;

    @Column
    private String soundUrl;

    @Column
    private LocalDateTime uploadAt; // TODO add new column in SQL table sounds
}
