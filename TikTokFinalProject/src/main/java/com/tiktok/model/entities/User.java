package com.tiktok.model.entities;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
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


    @Column(name = "verification_code")
    private String verificationCode;

    @OneToMany(mappedBy = "owner")
    private List<Video> videos;

    @ManyToMany
    @JoinTable(
            name = "video_likes",
            joinColumns = @JoinColumn(name = "owner_id"),
            inverseJoinColumns = @JoinColumn(name = "video_id"))
    private List<Video> likedVideos;

    @OneToMany(mappedBy = "owner")
    private List<Comment> comments;

    @ManyToMany
    @JoinTable(
            name = "comment_likes",
            joinColumns = @JoinColumn(name = "owner_id"),
            inverseJoinColumns = @JoinColumn(name = "comment_id"))
    private List<Comment> likedComments;

    @ManyToMany
    @JoinTable(
            name = "subscribers",
            joinColumns = @JoinColumn(name = "publisher_id"),
            inverseJoinColumns = @JoinColumn(name = "subscriber_id"))
    private List<User> subscribers;

    @ManyToMany(mappedBy = "subscribers")
    private List<User> subscribeTo;

    @OneToMany(mappedBy = "sender")
    List<Message> sent;

    @OneToMany(mappedBy = "receiver")
    List<Message> received;

}
