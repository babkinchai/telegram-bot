package com.example.telegrambot.repository;

import com.example.telegrambot.entity.BotUsers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BotUsersRepos extends JpaRepository<BotUsers,Long> {
}
