package com.tiktok.controller;

import com.tiktok.model.dto.comments.AddRequestCommentDTO;
import com.tiktok.model.dto.comments.AddResponseCommentDTO;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
public class CommentController extends GlobalController {

    @PostMapping("/videos/{videoId}")
    public AddResponseCommentDTO addComment(@PathVariable int videoId, HttpServletRequest request, @RequestBody AddRequestCommentDTO dto){
        int userId = getUserIdFromSession(request);
        return commentService.addComment(videoId, userId, dto);
    }
    @PostMapping("/videos/{videoId}/comments/{cid}")
    public AddResponseCommentDTO commentTheComment(@PathVariable int videoId, @PathVariable int cid,
                                                   HttpServletRequest request, @RequestBody AddRequestCommentDTO dto){
        int userId = getUserIdFromSession(request);
        return commentService.commentTheComment(videoId,userId,cid,dto);
    }

    @PutMapping ("/commenrs/{commentId}/likes")
    public String likeComment(@PathVariable int commentId, HttpServletRequest request){
        int userID = getUserIdFromSession(request);
        return commentService.likeComment(commentId, userID);
    }
}
