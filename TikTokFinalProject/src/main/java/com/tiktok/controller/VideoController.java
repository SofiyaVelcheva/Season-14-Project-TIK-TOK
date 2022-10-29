package com.tiktok.controller;

import com.tiktok.model.dto.comments.CommentWithoutVideoDTO;
import com.tiktok.model.dto.videoDTO.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
public class VideoController extends GlobalController {

    @PostMapping("users/{userId}/uploadVideo")
    public VideoWithoutOwnerDTO uploadVideo(@PathVariable int userId, @RequestParam(value = "file") MultipartFile file, @RequestParam(value = "isLive") Boolean isLve,
                                            @RequestParam(value = "isPrivate") Boolean isPrivate, @RequestParam(value = "description") String description,
                                            HttpServletRequest request) {
        int uid = getUserIdFromSession(request);
        return videoService.uploadVideo(uid, file, isLve, isPrivate, description);
    }

    @PutMapping("videos/{videoId}")
    public EditResponseVideoDTO editVideo(@PathVariable int videoId, @RequestBody EditRequestVideoDTO dto, HttpServletRequest request) {
        int userId = getUserIdFromSession(request);
        return videoService.editVideo(videoId, dto, userId);
    }

    @DeleteMapping("videos/{videoId}")
    public String deleteVideo(@PathVariable int videoId, HttpServletRequest request){
        int userId = getUserIdFromSession(request);
        return videoService.deleteVideo(videoId, userId);
    }

    @PutMapping("videos/{videoId}/likes")
    public String likeVideo (@PathVariable int videoId, HttpServletRequest request){
        int userId = getUserIdFromSession(request);
        return videoService.likeVideo(videoId, userId);
    }

    @GetMapping("/users/myVideos")
    public List<VideoWithoutOwnerDTO> showMyVideos(HttpServletRequest request){
        int userId = getUserIdFromSession(request);
        return videoService.showMyVideos(userId);
    }

    @GetMapping("/videos/{videoId}/comments")//commentController
    public List<CommentWithoutVideoDTO> showAllComments(@PathVariable int videoId){
        return videoService.showAllComments(videoId);
    }

    @GetMapping("/videos/{videoId}/commentsOrderByLastAdd") //commentController
    public List<CommentWithoutVideoDTO> showAllCommentsOrderByLastAdd (@PathVariable int videoId){
        return videoService.showAllCommentsOrderByLastAdd(videoId);
    }

    @GetMapping("/videos/krasi")
    public List<VideoWithoutOwnerDTO> showByKrasiRequst (@RequestBody RequestShowByDTO dto){
        return videoService.showByKrasiRequst(dto);
    }

    @GetMapping("/videos/sub")
    public ResponseEntity<List<VideoUploadResponseDTO>> getVideosPublishers(@RequestParam(value = "page", defaultValue = "0") int page,
                                                                            @RequestParam(value = "perPage", defaultValue = "10") int perPage,
                                                                            HttpServletRequest req) {
        return new ResponseEntity<>(videoService.getVideosPublishers(getUserIdFromSession(req), page, perPage), HttpStatus.OK);
    }

    @GetMapping("/videos/hashtag")
    public ResponseEntity<List<VideoUploadResponseDTO>> getAllVideosHashtag(@RequestParam(value = "text") String text,
                                                                            @RequestParam(value = "page", defaultValue = "0") int page,
                                                                            @RequestParam(value = "perPage", defaultValue = "10") int perPage) {
        return new ResponseEntity<>(videoService.getAllVideosHashtag(text, page, perPage), HttpStatus.OK);
    }



//    @GetMapping("/videos/showByLikes")
//    public List<VideoWithoutOwnerDTO> showAllByLikes(){
//        return videoService.showAllByLikes();
//    }
//
//    @GetMapping("/videos/showByComments")
//    public List<VideoWithoutOwnerDTO> showAllByComments (){
//        return videoService.showAllByComments();
//    }
//    @GetMapping("/videos/showByDate")
//    public List<VideoWithoutOwnerDTO> showAllByDate (){
//        return videoService.showAllByDate();
//    }
}