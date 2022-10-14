package com.tiktok.model.entities;

import lombok.Data;
import lombok.Generated;

import javax.persistence.*;

@Data
@Entity
@Table (name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
}
