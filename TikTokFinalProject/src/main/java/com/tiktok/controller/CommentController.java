package com.tiktok.controller;

import com.tiktok.model.dto.comments.AddRequestCommentDTO;
import com.tiktok.model.dto.comments.AddResponseCommentDTO;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
public class CommentController extends GlobalController {

    @PostMapping("/videos/{videoId}")
    public AddResponseCommentDTO addComment(@PathVariable int videoId,@RequestBody AddRequestCommentDTO dto, HttpServletRequest request){
        int userId = getUserIdFromSession(request);
        return commentService.addComment(videoId, userId, dto);
    }
    @PostMapping("/videos/{videoId}/comments/{commentId}")
    public AddResponseCommentDTO replyToComment(@PathVariable int videoId, @PathVariable int commentId,
                                                 @RequestBody AddRequestCommentDTO dto,  HttpServletRequest request){
        int userId = getUserIdFromSession(request);
        return commentService.replyToComment(videoId,userId,commentId,dto);
    }

    @PutMapping ("/comments/{commentId}/likes")
    public String likeComment(@PathVariable int commentId, HttpServletRequest request){
        int userID = getUserIdFromSession(request);
        return commentService.likeComment(commentId, userID);
    }

    @DeleteMapping ("/videos/{videoId}/comments/{commentId}")
    public String deleteComment (@PathVariable int videoId, @PathVariable int commentId, HttpServletRequest request){
        int userId = getUserIdFromSession(request);
        return commentService.deleteComment(videoId, commentId, userId);
    }
}
