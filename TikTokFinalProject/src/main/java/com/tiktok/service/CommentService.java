package com.tiktok.service;

import com.tiktok.model.dto.comments.AddRequestCommentDTO;
import com.tiktok.model.dto.comments.AddResponseCommentDTO;
import com.tiktok.model.dto.comments.CommentWithoutUserDTO;
import com.tiktok.model.entities.Comment;
import com.tiktok.model.entities.User;
import com.tiktok.model.entities.Video;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommentService extends GlobalService {
    public AddResponseCommentDTO addComment(int videoId, int userId, AddRequestCommentDTO dto) {
        Video video = getVideoById(videoId);
        User user = getUserById(userId);
        Comment comment = new Comment();
        comment.setVideo(video);
        comment.setOwner(user);
        comment.setUploadAt(LocalDateTime.now());
        comment.setText(dto.getText());
        commentRepository.save(comment);
        return modelMapper.map(comment, AddResponseCommentDTO.class);
    }

    public AddResponseCommentDTO commentTheComment(int videoId, int userId, int commentId, AddRequestCommentDTO dto) {
        Video video = getVideoById(videoId);
        User user = getUserById(userId);
        Comment parent = getCommentById(commentId);
        Comment child = new Comment();
        child.setVideo(video);
        child.setOwner(user);
        child.setParentId(parent);
        child.setUploadAt(LocalDateTime.now());
        child.setText(dto.getText());
        commentRepository.save(child);
        AddResponseCommentDTO response = modelMapper.map(child, AddResponseCommentDTO.class);
        response.setParentId(modelMapper.map(child.getParentId(), CommentWithoutUserDTO.class));
        return response;
    }

    public String likeComment(int commentId, int userID) {
        User user = getUserById(userID);
        Comment comment = getCommentById(commentId);
        if (user.getLikedComments().contains(comment)){
            user.getLikedComments().remove(comment);
        }else {
            user.getLikedComments().add(comment);
        }
        userRepository.save(user);
        return "Comment has " + user.getLikedComments().size() + " likes.";
    }



}
