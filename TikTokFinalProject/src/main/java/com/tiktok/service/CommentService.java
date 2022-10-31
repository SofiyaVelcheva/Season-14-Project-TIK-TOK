package com.tiktok.service;

import com.tiktok.model.dto.comments.*;
import com.tiktok.model.dto.user.PublisherUserDTO;
import com.tiktok.model.entities.Comment;
import com.tiktok.model.entities.User;
import com.tiktok.model.entities.Video;
import com.tiktok.model.exceptions.BadRequestException;
import com.tiktok.model.exceptions.UnauthorizedException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CommentService extends GlobalService {
    public AddResponseCommentDTO addComment(int videoId, int userId, CommentWithTextDTO dto) {
        Video video = getVideoById(videoId);
        if (video.isPrivate()) { //even the owner can't add a comment to private videos
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

    public AddResponseCommentDTO replyToComment(int userId, int commentId, CommentWithTextDTO dto) {
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
        response.setParentId(modelMapper.map(child.getParentId().getOwner(), PublisherUserDTO.class));
        return response;
    }

    public CommentWithTextDTO likeComment(int commentId, int userID) {
        User user = getUserById(userID);
        Comment comment = getCommentById(commentId);
        if (user.getLikedComments().contains(comment)) {
            user.getLikedComments().remove(comment);
        } else {
            user.getLikedComments().add(comment);
        }
        userRepository.save(user);
        return new CommentWithTextDTO("Comment has " + user.getLikedComments().size() + " likes.");
    }

    @Transactional
    public CommentWithTextDTO deleteComment(int commentId, int userId) {
        Comment comment = getCommentById(commentId);
        if (comment.getVideo().getOwner().getId() != userId && comment.getOwner().getId() != userId) {
            throw new UnauthorizedException("You can't delete comment which isn't yours!");
        }
        List<Comment> comments = comment.getChildComments();
        commentRepository.deleteAll(comments); // delete the children
        commentRepository.delete(comment);
        return new CommentWithTextDTO("The comment is deleted!");
    }

    public List<CommentWithoutVideoDTO> showAllComments(int videoId, int pageNumber, int commentsPerPage) {
        Pageable page = PageRequest.of(pageNumber,commentsPerPage);
        Video video = getVideoById(videoId);
        List<Comment> comments = commentRepository.findAllByVideo(video, page);
        return mapDto(comments);
    }

    public List<CommentWithoutVideoDTO> showAllCommentsOrderByLastAdd(int videoId, int pageNumber, int commentsPerPage) {
        Pageable page = PageRequest.of(pageNumber,commentsPerPage);
        List<Comment> comments = commentRepository.findParentCommentsOrderByDate(videoId, page);
        return mapDto(comments);
    }

    public List<CommentWithoutUserDTO> repliesToComment(int commentId, int pageNumber, int commentsPerPage) {
        pageable = PageRequest.of(pageNumber,commentsPerPage);
        List<Comment> replies = commentRepository.findAllRepliesToComment(commentId, pageable);
        List<CommentWithoutUserDTO> dtoComments = new ArrayList<>();
        for (Comment comment : replies){
            CommentWithoutUserDTO dto = modelMapper.map(comment, CommentWithoutUserDTO.class);
            dto.setOwner(modelMapper.map(comment.getOwner(),PublisherUserDTO.class));
            dtoComments.add(dto);
        }
        return dtoComments;
    }

    private List<CommentWithoutVideoDTO> mapDto (List<Comment> comments){
        List<CommentWithoutVideoDTO> dtoComments = new ArrayList<>();
        for (Comment comment : comments){
            CommentWithoutVideoDTO dto = modelMapper.map(comment, CommentWithoutVideoDTO.class);
            dtoComments.add(dto);
        }
        return dtoComments;
    }
}
