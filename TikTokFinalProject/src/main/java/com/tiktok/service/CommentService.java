package com.tiktok.service;

import com.tiktok.model.dto.comments.*;
import com.tiktok.model.entities.Comment;
import com.tiktok.model.entities.User;
import com.tiktok.model.entities.Video;
import com.tiktok.model.exceptions.BadRequestException;
import com.tiktok.model.exceptions.UnauthorizedException;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CommentService extends GlobalService {
    public AddResponseCommentDTO addComment(int videoId, int userId, AddRequestCommentDTO dto) {
        Video video = getVideoById(videoId);
        if (video.isPrivate()) { //even the owner can't add a comment
            throw new UnauthorizedException("The video is locked by owner!");
        }
        User user = getUserById(userId);
        Comment comment = modelMapper.map(dto, Comment.class);
        comment.setVideo(video);
        comment.setOwner(user);
        comment.setUploadAt(LocalDateTime.now());
        comment.setText(dto.getText());
        commentRepository.save(comment);
        return modelMapper.map(comment, AddResponseCommentDTO.class);
    }

    public AddResponseCommentDTO replyToComment(int userId, int commentId, AddRequestCommentDTO dto) {
        User user = getUserById(userId);
        Comment parent = getCommentById(commentId);
        if (parent.getParentId() != null) {
            throw new BadRequestException("The comment you try to reply is already a reply.");
        }
        Comment child = modelMapper.map(dto, Comment.class);
        child.setVideo(parent.getVideo());
        child.setOwner(user);
        child.setParentId(parent);
        child.setUploadAt(LocalDateTime.now());
        commentRepository.save(child);
        parent.getChildComments().add(child);
        AddResponseCommentDTO response = modelMapper.map(child, AddResponseCommentDTO.class);
        response.setParentId(modelMapper.map(child.getParentId(), CommentWithoutUserDTO.class));
        return response;
    }

    public CommentResponseMessageDTO likeComment(int commentId, int userID) {
        User user = getUserById(userID);
        Comment comment = getCommentById(commentId);
        if (user.getLikedComments().contains(comment)) {
            user.getLikedComments().remove(comment);
        } else {
            user.getLikedComments().add(comment);
        }
        userRepository.save(user);
        return new CommentResponseMessageDTO("Comment has " + user.getLikedComments().size() + " likes.");
    }

    @Transactional
    public CommentResponseMessageDTO deleteComment(int commentId, int userId) {
        Comment comment = getCommentById(commentId);
        if (comment.getVideo().getOwner().getId() != userId && comment.getOwner().getId() != userId) {
            throw new UnauthorizedException("You can't delete comment which isn't yours!");
        }
        List<Comment> comments = comment.getChildComments();
        commentRepository.deleteAll(comments); // delete the children
        commentRepository.delete(comment);
        return new CommentResponseMessageDTO("The comment is deleted!");
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
        List<Comment> comments = commentRepository.findParentCommentsOrderByDate(videoId);
        List<CommentWithoutVideoDTO> allComments = new ArrayList<>();
        for (Comment comment : comments) {
            CommentWithoutVideoDTO dto = modelMapper.map(comment, CommentWithoutVideoDTO.class);
            allComments.add(dto);
        }
        return allComments;
    }
}
