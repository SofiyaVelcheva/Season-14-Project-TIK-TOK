package com.tiktok.model.repository;

import com.tiktok.model.entities.Hashtag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HashtagRepository extends JpaRepository<Hashtag, Integer> {

    Optional<Hashtag> findHashtagByText(String text);

}
