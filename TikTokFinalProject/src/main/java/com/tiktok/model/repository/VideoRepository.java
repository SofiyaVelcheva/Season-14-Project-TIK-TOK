package com.tiktok.model.repository;

import com.tiktok.model.entities.User;
import com.tiktok.model.entities.Video;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoRepository extends JpaRepository<Video, Integer> {

    List<Video> findAllByOwner(User owner);

    @Query(value = "SELECT * FROM videos AS v WHERE v.is_live is true", nativeQuery = true)
    List<Video> getAllLiveVideos(Pageable page);

    @Query(value = "SELECT * FROM videos WHERE owner_id IN " +
            "(SELECT s.publisher_id FROM subscribers As s WHERE s.subscriber_id = :userId) " +
            "AND is_private IS FALSE ORDER BY upload_at DESC", nativeQuery = true)
    List<Video> getAllVideosPublishers(@Param("userId") int userId, Pageable page);

    @Query(value = "SELECT * FROM videos AS v " +
            "JOIN  users AS u ON (v.owner_id = u.id) " +
            "WHERE u.id = :userId", nativeQuery = true)
    List<Video> showMyVideos(@Param("userId") int userId, Pageable page);



}

