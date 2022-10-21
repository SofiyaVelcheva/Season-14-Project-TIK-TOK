package com.tiktok.model.repository;

import com.tiktok.model.dto.comments.CommentWithoutVideoDTO;
import com.tiktok.model.entities.Comment;
import com.tiktok.model.entities.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {

    List<Comment> findAllByVideo(Video video);
}
