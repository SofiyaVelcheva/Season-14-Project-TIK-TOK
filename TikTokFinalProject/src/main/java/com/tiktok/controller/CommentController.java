package com.tiktok.controller;

import com.tiktok.model.dto.comments.AddResponseCommentDTO;
import com.tiktok.model.dto.comments.CommentWithTextDTO;
import com.tiktok.model.dto.comments.CommentWithoutUserDTO;
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
                                                            @RequestBody CommentWithTextDTO dto,
                                                            HttpServletRequest request) {
        return new ResponseEntity<>(commentService.addComment(videoId, getUserIdFromSession(request), dto), HttpStatus.CREATED);
    }

    @PostMapping("/comments/{commentId}")
    public ResponseEntity<AddResponseCommentDTO> replyToComment(@PathVariable int commentId,
                                                                @RequestBody CommentWithTextDTO dto,
                                                                HttpServletRequest request) {
        return new ResponseEntity<>(commentService.replyToComment(getUserIdFromSession(request), commentId, dto), HttpStatus.CREATED);
    }

    @PutMapping("/comments/{commentId}/likes")
    public ResponseEntity<CommentWithTextDTO> likeComment(@PathVariable int commentId,
                                                          HttpServletRequest request) {
        return new ResponseEntity<>(commentService.likeComment(commentId, getUserIdFromSession(request)), HttpStatus.OK);
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<CommentWithTextDTO> deleteComment(@PathVariable int commentId,
                                                            HttpServletRequest request) {
        return new ResponseEntity<>(commentService.deleteComment(commentId, getUserIdFromSession(request)), HttpStatus.OK);
    }

    @PostMapping("/videos/{videoId}/comments")
    public ResponseEntity<List<CommentWithoutVideoDTO>> showAllComments(@PathVariable int videoId,
                                                                        @RequestParam(defaultValue = "0") int pageNumber,
                                                                        @RequestParam(defaultValue = "3") int commentsPerPage) {
        return new ResponseEntity<>(commentService.showAllComments(videoId, pageNumber, commentsPerPage), HttpStatus.OK);
    }

    @PostMapping("/comments/{commentId}/replies")
    public ResponseEntity<List<CommentWithoutUserDTO>> repliesToComment(@PathVariable int commentId,
                                                                        @RequestParam(defaultValue = "0") int pageNumber,
                                                                        @RequestParam(defaultValue = "3") int commentsPerPage) {
        return new ResponseEntity<>(commentService.repliesToComment(commentId, pageNumber, commentsPerPage), HttpStatus.OK);
    }


    @PostMapping("/videos/{videoId}/commentsOrderByLastAdd")
    public ResponseEntity<List<CommentWithoutVideoDTO>> showAllCommentsOrderByLastAdd(@PathVariable int videoId,
                                                                                      @RequestParam(defaultValue = "0") int pageNumber,
                                                                                      @RequestParam(defaultValue = "3") int commentsPerPage) {
        return new ResponseEntity<>(commentService.showAllCommentsOrderByLastAdd(videoId, pageNumber, commentsPerPage), HttpStatus.OK);
    }


}
