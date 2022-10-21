package com.tiktok.model.repository;

import com.tiktok.model.entities.User;
import com.tiktok.model.entities.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoRepository extends JpaRepository<Video, Integer> {

    List<Video> findAllByOwner(User owner);

}
