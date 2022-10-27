package com.tiktok.controller;

import com.tiktok.model.dto.comments.CommentWithoutVideoDTO;
import com.tiktok.model.dto.videoDTO.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
public class VideoController extends GlobalController {

    @PostMapping("users/{userId}/uploadVideo")
    @ResponseStatus(HttpStatus.CREATED)
    public PostResponsVideoDTO uploadVideo(@PathVariable int userId, @RequestParam(value = "file") MultipartFile file, @RequestParam(value = "isLive") Boolean isLve,
                                           @RequestParam(value = "isPrivate") Boolean isPrivate, @RequestParam(value = "description") String description,
                                           HttpServletRequest request) {
        int uid = getUserIdFromSession(request);
        return videoService.uploadVideo(uid, file, isLve, isPrivate, description);
    }

    @PutMapping("videos/{videoId}")
    @ResponseStatus(HttpStatus.OK)
    public EditResponseVideoDTO editVideo(@PathVariable int videoId, @RequestBody EditRequestVideoDTO dto, HttpServletRequest request) {
        int userId = getUserIdFromSession(request);
        return videoService.editVideo(videoId, dto, userId);
    }

    @DeleteMapping("videos/{videoId}")
    @ResponseStatus(HttpStatus.OK)
    public String deleteVideo(@PathVariable int videoId, HttpServletRequest request) {
        int userId = getUserIdFromSession(request);
        return videoService.deleteVideo(videoId, userId);
    }

    @PutMapping("videos/{videoId}/like")
    @ResponseStatus(HttpStatus.OK)
    public String likeVideo(@PathVariable int videoId, HttpServletRequest request) {
        int userId = getUserIdFromSession(request);
        return videoService.likeVideo(videoId, userId);
    }

    @PostMapping("/users/myVideos")
    @ResponseStatus(HttpStatus.OK)
    public List<VideoWithoutOwnerDTO> showMyVideos(@RequestParam(defaultValue = "0") int pageNumber,
                                                   @RequestParam(defaultValue = "3") int videosPerPage,
                                                   HttpServletRequest request) {
        int userId = getUserIdFromSession(request);
        return videoService.showMyVideos(userId, pageNumber, videosPerPage);
    }

    @GetMapping("/videos/krasi")
    @ResponseStatus(HttpStatus.I_AM_A_TEAPOT)
    public List<VideoWithoutOwnerDTO> showByKrasiRequst(@RequestBody RequestShowByDTO dto) {
        return videoService.showByKrasiRequst(dto);
    }


}