package com.tiktok.service;

import com.tiktok.model.dto.comments.AddRequestCommentDTO;
import com.tiktok.model.dto.comments.AddResponseCommentDTO;
import com.tiktok.model.dto.comments.CommentWithoutUserDTO;
import com.tiktok.model.dto.comments.CommentWithoutVideoDTO;
import com.tiktok.model.entities.Comment;
import com.tiktok.model.entities.User;
import com.tiktok.model.entities.Video;
import com.tiktok.model.exceptions.BadRequestException;
import com.tiktok.model.exceptions.UnauthorizedException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CommentService extends GlobalService {
    public AddResponseCommentDTO addComment(int videoId, int userId, AddRequestCommentDTO dto) {
        Video video = getVideoById(videoId);
        if (video.isPrivate()) { //even the owner can't add a comment
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
        if (parent.getParentId() != null) {
            throw new BadRequestException("You can't add comment to replay comment!");
        }
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
        if (user.getLikedComments().contains(comment)) {
            user.getLikedComments().remove(comment);
        } else {
            user.getLikedComments().add(comment);
        }
        userRepository.save(user);
        return "Comment has " + user.getLikedComments().size() + " likes.";
    }

    @Transactional(rollbackOn = {SQLException.class})
    public String deleteComment(int commentId, int userId) {
        User user = getUserById(userId);
        Comment comment = getCommentById(commentId);
        if (comment.getVideo().getOwner().getId() != userId && comment.getOwner().getId() != userId) {
            throw new UnauthorizedException("You can not delete comment that isn't yours!");
        }
        List<Comment> comments = comment.getChildComments();
        commentRepository.deleteAll(comments);
        commentRepository.delete(comment);
        return "The comment is deleted!";
    }


    public List<CommentWithoutVideoDTO> showAllComments(int videoId) {
        Video video = getVideoById(videoId);
        List<Comment> comments = commentRepository.findAllByVideo(video);
        List<CommentWithoutVideoDTO> videoWithComments = new ArrayList<>();
        for (Comment comment : comments) {
            CommentWithoutVideoDTO dto = modelMapper.map(comment, CommentWithoutVideoDTO.class);
            videoWithComments.add(dto);
        }
        return videoWithComments;
    }


    public List<CommentWithoutVideoDTO> showAllCommentsOrderByLastAdd(int videoId) {
        Video video = getVideoById(videoId); // if video exists
        List<Comment> comments = commentRepository.findParentCommentsOrderByDate(videoId);
        System.out.println(comments.size());
        List<CommentWithoutVideoDTO> allComments = new ArrayList<>();
        for (Comment comment : comments) {
            CommentWithoutVideoDTO dto = modelMapper.map(comment, CommentWithoutVideoDTO.class);
            allComments.add(dto);
        }
        return allComments;
    }
}
