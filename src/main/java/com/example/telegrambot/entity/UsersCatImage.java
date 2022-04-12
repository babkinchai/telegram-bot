package com.example.telegrambot.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "usr_cat_name")
@Data
public class UsersCatImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    private BotUsers botUsers;

    @Column(name = "image_name")
    private String imageName;

    @Column(name = "file_id")
    private String fileId;
}
