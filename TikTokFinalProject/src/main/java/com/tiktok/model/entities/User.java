package com.tiktok.model.entities;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.awt.print.Pageable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column
    private String username;
    @Column
    private String password;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Column
    private String email;
    @Column(name = "phone_number")
    private String phoneNumber;
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;
    @Column(name = "verified_email")
    private boolean verifiedEmail;
    @Column(name = "profile_photo_url")
    private String photoURL;
    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @OneToMany(mappedBy = "owner")
    private List<Video> videos;
    @ManyToMany
    @JoinTable(
            name = "video_likes",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "video_id"))
    private List<Video> likedVideos;

    @OneToMany(mappedBy = "owner")
    private List<Comment> comments;

    @ManyToMany
    @JoinTable(
            name = "comment_likes",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "comment_id"))
    private List<Comment> likedComments;

    @ManyToMany
    @JoinTable(
            name = "subscribers",
            joinColumns = @JoinColumn(name = "publisher_id"),
            inverseJoinColumns = @JoinColumn(name = "subscriber_id"))
    private List<User> subscribers = new ArrayList<>();

    @ManyToMany(mappedBy = "subscribers")
    private List<User> subscribeTo = new ArrayList<>();




}
