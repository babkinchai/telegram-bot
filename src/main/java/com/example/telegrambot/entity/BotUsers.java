package com.example.telegrambot.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity()
@Data
@Table(name = "usr")
public class BotUsers {
    @Id
    private Long id;

    @Column(name = "username")
    private String username;

    @ManyToMany(targetEntity = BotUsers.class,fetch = FetchType.EAGER)
    private List<BotUsers> subUser;

}
