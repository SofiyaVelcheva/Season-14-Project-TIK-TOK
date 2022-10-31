package com.tiktok.model.repository;

import com.tiktok.model.entities.Comment;
import com.tiktok.model.entities.Video;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findAllByVideo(Video video, Pageable page);

    @Query(value = "SELECT * FROM comments AS c JOIN users WHERE video_id = :videoId AND " +
            "c.parent_id is null GROUP BY c.id ORDER BY upload_at DESC", nativeQuery = true)
    List<Comment> findParentCommentsOrderByDate(@Param("videoId") Integer videoId, Pageable page);

    @Query(value = "SELECT * FROM comments AS c WHERE c.parent_id = :commentId", nativeQuery = true)
    List<Comment> findAllRepliesToComment(@Param("commentId") Integer commentId,Pageable page);
}
