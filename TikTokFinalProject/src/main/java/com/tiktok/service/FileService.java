package com.tiktok.service;

import com.tiktok.model.dto.user.UserResponseDTO;
import com.tiktok.model.entities.User;
import com.tiktok.model.exceptions.BadRequestException;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

@Service
public class FileService extends GlobalService {

    private static final int WIDTH_PIXELS = 20;
    private static final int HEIGHT_PIXELS = 20;

    public String createVideo(MultipartFile file, int userId) {
        try {
            String ext = FilenameUtils.getExtension(file.getOriginalFilename());
            if (!validateVideoFormat(ext)) {
                throw new BadRequestException("The format of the video is not allowed.");
            }
            String path = "videos" + File.separator + userId + "_" + System.nanoTime() + "." + ext;
            File newFile = new File(path);
            Files.copy(file.getInputStream(), newFile.toPath());
            return path;
        } catch (
                IOException e) {
            throw new BadRequestException(e.getMessage());
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

    public UserResponseDTO uploadProfilePhoto(int userIdFromSession, MultipartFile file) {
        try {
            User user = getUserById(userIdFromSession);
            checkContentType(file);
            checkSizePhoto(file.getInputStream());
            String ext = FilenameUtils.getExtension(file.getOriginalFilename());
            String name = userIdFromSession +"_"+ System.nanoTime() + "." + ext;
            String filePath = "photos" + File.separator + name;
            File f = new File(filePath);
             if (user.getPhotoURL() != null) {
                File old = new File("photos" + File.separator + user.getPhotoURL());
                old.delete();
            }
            user.setPhotoURL(name);
            userRepository.save(user);
            return getUserResponseDTO(user);
        } catch (IOException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    private void checkContentType(MultipartFile file) {
        if (!file.getContentType().contains("image/jpeg")
                && !file.getContentType().contains("image/jpg")
                && !file.getContentType().contains("image/png")) {
            throw new BadRequestException("Photo must be in .jpg, .jpeg or .png format.");
        }
    }

    private void checkSizePhoto(InputStream inputStreamFile) throws IOException {
        BufferedImage buffImage = ImageIO.read(inputStreamFile);
        int width = buffImage.getWidth();
        int height = buffImage.getHeight();
        if (width < WIDTH_PIXELS || height < HEIGHT_PIXELS) {
            throw new BadRequestException("Photos must be at least 20x20 pixels.");
        }
    }
}