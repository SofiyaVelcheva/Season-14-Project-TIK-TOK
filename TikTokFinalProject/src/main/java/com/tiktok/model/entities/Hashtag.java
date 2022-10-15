package com.tiktok.model.entities;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "hashtags")
public class Hashtag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
}
