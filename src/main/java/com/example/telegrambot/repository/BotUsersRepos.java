package com.example.telegrambot.repository;

import com.example.telegrambot.entity.BotUsers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BotUsersRepos extends JpaRepository<BotUsers,Long> {

    Optional<BotUsers> findById(Long id);
    Optional<BotUsers> findByUsername(String username);
    boolean existsById(Long id);
}
