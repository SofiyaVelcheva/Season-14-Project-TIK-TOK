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

    @Query(value = "SELECT * FROM videos WHERE owner_id IN " +
            "(SELECT s.publisher_id FROM subscribers As s WHERE s.subscriber_id = :userId) " +
            "AND is_private IS FALSE ORDER BY upload_at desc", nativeQuery = true)
    List<Video> getAllVideosPublishers(@Param("userId") int userId, Pageable pageable);

    @Query(value = "SELECT * FROM videos AS v JOIN users AS u ON (v.owner_id = u.id) " +
            "JOIN comments AS c ON (v.id = c.video_id) " +
            "WHERE v.description Like \"%:=title%\" " +
            "AND u.username = \":=username\" " +
            "AND v.upload_at >= ':=uploadAt' AND v.upload_at <= ':=uploadTo'" +
            "AND c.parent_id is null GROUP BY v.id;", nativeQuery = true)
    List<Video> KrasiRequest(@Param("title") String title, @Param("username") String username,
                             @Param("uploadAt") String uploadAt, @Param("uploadTo") String uploadTo);// trow an exp .QueryException: Space is not allowed after parameter prefix ':'

    @Query(value = "SELECT * FROM videos as v WHERE id IN (SELECT id FROM videos_with_hashtags)" +
            "AND description LIKE :hashtag ORDER BY upload_at desc", nativeQuery = true)
    List<Video> getAllHashtagByName(@Param("hashtag") String hashtag, Pageable pageable);

}

