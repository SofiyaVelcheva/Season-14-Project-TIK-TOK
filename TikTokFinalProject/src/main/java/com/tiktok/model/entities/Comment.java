package com.tiktok.model.entities;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity (name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
}
