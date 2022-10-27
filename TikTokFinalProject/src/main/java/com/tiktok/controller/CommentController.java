package com.tiktok.controller;

import com.tiktok.model.dto.comments.AddRequestCommentDTO;
import com.tiktok.model.dto.comments.AddResponseCommentDTO;
import com.tiktok.model.dto.comments.CommentWithoutVideoDTO;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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

    @DeleteMapping ("/comments/{commentId}")
    public String deleteComment (@PathVariable int commentId, HttpServletRequest request){
        int userId = getUserIdFromSession(request);
        return commentService.deleteComment(commentId, userId);
    }
    @GetMapping("/videos/{videoId}/comments")
    public List<CommentWithoutVideoDTO> showAllComments(@PathVariable int videoId){
        return commentService.showAllComments(videoId);
    }

    @GetMapping("/videos/{videoId}/commentsOrderByLastAdd")
    public List<CommentWithoutVideoDTO> showAllCommentsOrderByLastAdd (@PathVariable int videoId){
        return commentService.showAllCommentsOrderByLastAdd(videoId);
    }

}
