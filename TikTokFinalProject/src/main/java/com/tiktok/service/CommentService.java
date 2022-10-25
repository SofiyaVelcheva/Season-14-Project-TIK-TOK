package com.tiktok.service;

import com.tiktok.model.dto.comments.AddRequestCommentDTO;
import com.tiktok.model.dto.comments.AddResponseCommentDTO;
import com.tiktok.model.dto.comments.CommentWithoutUserDTO;
import com.tiktok.model.entities.Comment;
import com.tiktok.model.entities.User;
import com.tiktok.model.entities.Video;
import com.tiktok.model.exceptions.UnauthorizedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CommentService extends GlobalService {
    public AddResponseCommentDTO addComment(int videoId, int userId, AddRequestCommentDTO dto) {
        Video video = getVideoById(videoId);
        if (video.isPrivate()){ //even the owner can't add a comment
            throw new UnauthorizedException("The video is locked by owner");
        }
        User user = getUserById(userId);
        Comment comment = new Comment();
        comment.setVideo(video);
        comment.setOwner(user);
        comment.setUploadAt(LocalDateTime.now());
        comment.setText(dto.getText());
        commentRepository.save(comment);
        return modelMapper.map(comment, AddResponseCommentDTO.class);
    }

    public AddResponseCommentDTO replyToComment(int videoId, int userId, int commentId, AddRequestCommentDTO dto) {
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


    public String deleteComment(int videoId, int commentId, int userId) {
        Video video = getVideoById(videoId);
        Comment comment = getCommentById(commentId);
        User user = getUserById(userId);
        if (video.getId() != comment.getVideo().getId()){
            throw new UnauthorizedException("The comment you try to delete isn't exists.");
        }
        if(user.getId() != video.getOwner().getId()) {
            if (user.getId() != comment.getOwner().getId()) {
                throw new UnauthorizedException("The comment you try to delete isn't yours.");
            }
        }
        commentRepository.delete(comment);
        return "The comment is deleted!";
    }
}
