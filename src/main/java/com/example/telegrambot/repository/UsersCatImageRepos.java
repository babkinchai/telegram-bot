package com.example.telegrambot.repository;

import com.example.telegrambot.entity.UsersCatImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsersCatImageRepos extends JpaRepository<UsersCatImage,Long>{

    @Query(nativeQuery = true, value = "SELECT * FROM usr_cat_name ORDER BY RANDOM() LIMIT 1")
    List<UsersCatImage> getRundom();

    public boolean existsUsersCatImageByImageName(String imageName);
}
