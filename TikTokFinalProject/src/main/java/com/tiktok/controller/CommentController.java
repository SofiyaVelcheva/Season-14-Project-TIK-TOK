package com.tiktok.controller;

import com.tiktok.model.dto.comments.AddRequestCommentDTO;
import com.tiktok.model.dto.comments.AddResponseCommentDTO;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
}
