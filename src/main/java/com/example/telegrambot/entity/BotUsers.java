package com.example.telegrambot.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity()
@Data
@Table(name = "usr")
public class BotUsers {
    @Id
    private Long id;

    @Column(name = "username")
    private String username;

}
