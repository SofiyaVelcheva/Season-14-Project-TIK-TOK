package com.tiktok.model.entities;


import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "sounds")
public class Sound {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
}
