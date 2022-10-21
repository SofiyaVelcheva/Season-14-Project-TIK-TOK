package com.tiktok.model.entities;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "videos")
@ToString
public class Video {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "owner_id")
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

    @ManyToMany(mappedBy = "likedVideos")
    List<User> likers;

    @OneToMany
    List<Comment> comments;


}
