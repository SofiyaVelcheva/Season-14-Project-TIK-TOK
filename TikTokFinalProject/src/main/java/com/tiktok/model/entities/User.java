package com.tiktok.model.entities;

import lombok.Data;
import lombok.Generated;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Entity
@Table (name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column
    private String username;
    @Column
    private String password;
    @Column
    private String firstName;
    @Column
    private String lastName;
    @Column
    private String email;
    @Column
    private String phoneNumber;
    @Column
    private LocalDate dateOfBirth;
    @Column
    private boolean verifiedEmail;


}
