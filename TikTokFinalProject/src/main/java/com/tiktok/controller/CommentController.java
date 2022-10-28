package com.tiktok.controller;

import com.tiktok.model.dto.comments.AddRequestCommentDTO;
import com.tiktok.model.dto.comments.AddResponseCommentDTO;
import com.tiktok.model.dto.comments.CommentResponseMessageDTO;
import com.tiktok.model.dto.comments.CommentWithoutVideoDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
public class CommentController extends GlobalController {

    @PostMapping("/videos/{videoId}")
    public ResponseEntity<AddResponseCommentDTO> addComment(@PathVariable int videoId,
                                                            @RequestBody AddRequestCommentDTO dto,
                                                            HttpServletRequest request) {
        int userId = getUserIdFromSession(request);
        return new ResponseEntity<>(commentService.addComment(videoId, userId, dto), HttpStatus.CREATED);
    }

    @PostMapping("/comments/{commentId}")
    public ResponseEntity<AddResponseCommentDTO> replyToComment(@PathVariable int commentId,
                                                                @RequestBody AddRequestCommentDTO dto,
                                                                HttpServletRequest request) {
        int userId = getUserIdFromSession(request);
        return new ResponseEntity<>(commentService.replyToComment(userId, commentId, dto), HttpStatus.CREATED);
    }

    @PutMapping("/comments/{commentId}/likes")
    public ResponseEntity<CommentResponseMessageDTO> likeComment(@PathVariable int commentId,
                                                                 HttpServletRequest request) {
        int userID = getUserIdFromSession(request);
        return new ResponseEntity<>(commentService.likeComment(commentId, userID), HttpStatus.OK);
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<CommentResponseMessageDTO> deleteComment(@PathVariable int commentId,
                                                                   HttpServletRequest request) {
        int userId = getUserIdFromSession(request);
        return new ResponseEntity<>(commentService.deleteComment(commentId, userId), HttpStatus.OK);
    }

    @GetMapping("/videos/{videoId}/comments")
    public ResponseEntity<List<CommentWithoutVideoDTO>> showAllComments(@PathVariable int videoId) {
        return new ResponseEntity<>(commentService.showAllComments(videoId), HttpStatus.OK);
    }

    @GetMapping("/videos/{videoId}/commentsOrderByLastAdd")
    public ResponseEntity<List<CommentWithoutVideoDTO>> showAllCommentsOrderByLastAdd(@PathVariable int videoId) {
        return new ResponseEntity<>(commentService.showAllCommentsOrderByLastAdd(videoId), HttpStatus.OK);
    }

}
