package com.tiktok.service;

import com.tiktok.model.entities.User;
import com.tiktok.model.entities.Video;
import com.tiktok.model.exceptions.BadRequestException;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Service
public class FileService extends GlobalService {

    public String createVideo(MultipartFile file, int userId) {
        try {
            String ext = FilenameUtils.getExtension(file.getOriginalFilename());
            if (!validateVideoFormat(ext)) {
                throw new BadRequestException("The format of the video is not allowed.");
            }
            String path = "videos" + File.separator + userId + "_" + System.nanoTime() + "." + ext;
            File newFile = new File(path);
            Files.copy(file.getInputStream(), newFile.toPath());
            Video video = new Video();
            if (video.getVideoUrl() != null) {
                File old = new File(video.getVideoUrl());
                old.delete();
            }
            return path;
        } catch (
                IOException e) {
            throw new BadRequestException(e.getMessage(), e);
        }

    }

    private boolean validateVideoFormat(String ext) {
        //TikTok supports the following video file types: .mp4, .mov, .mpeg, .3gp, .avi
        if (ext.equals("mp4") || ext.equals("mov") || ext.equals("mpeg")
                || ext.equals("3gp") || ext.equals("avi")) {
            return true;
        }
        return false;
    }
}