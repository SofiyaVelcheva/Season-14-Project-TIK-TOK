package com.tiktok.model.repository;

import com.tiktok.model.entities.User;
import com.tiktok.model.entities.Video;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.lang.invoke.CallSite;
import java.util.List;

@Repository
public interface VideoRepository extends JpaRepository<Video, Integer> {

    List<Video> findAllByOwner(User owner);

    Video getVideoById(int id);


    @Query(value = "SELECT * FROM videos WHERE owner_id IN " +
            "(SELECT s.publisher_id FROM subscribers As s WHERE s.subscriber_id = :userId) " +
            "AND is_private IS FALSE ORDER BY upload_at desc", nativeQuery = true)
    List<Video> getAllVideosPublishers(@Param("userId") int userId, Pageable pageable);



}

