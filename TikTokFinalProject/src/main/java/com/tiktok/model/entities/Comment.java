package com.tiktok.model.entities;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity (name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column
    private String text;
    @Column
    private LocalDateTime uploadAt;
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;
    @ManyToOne
    @JoinColumn(name = "video_id")
    private Video video;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Comment parentId; //comment with parent

    @OneToMany
    @JoinColumn (name = "parent_id")
    private List<Comment> childComments; // list with child comments

    @ManyToMany (mappedBy = "likedComments")
    private List<User> likers;

}
