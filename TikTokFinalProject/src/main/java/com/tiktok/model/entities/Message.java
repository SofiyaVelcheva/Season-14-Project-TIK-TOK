package com.tiktok.model.entities;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity (name = "messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

}
