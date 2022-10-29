package com.tiktok.model.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity (name = "hashtags")
public class Hashtag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private String text;

    @ManyToMany(mappedBy = "hashtags")
    private List<Video> videos;

}
