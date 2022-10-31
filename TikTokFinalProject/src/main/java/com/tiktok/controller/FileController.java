package com.tiktok.controller;


import com.tiktok.model.exceptions.BadRequestException;
import com.tiktok.model.exceptions.NotFoundException;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@RestController
public class FileController extends GlobalController {

    @GetMapping("/images/{fileName}")
    public void getProfilePhoto(@PathVariable String fileName,
                                HttpServletResponse response) {
        show(fileName, "photos", "Photo", response);
    }

    @GetMapping("videos/{fileName}")
    public void showVideo(@PathVariable String fileName,
                          HttpServletResponse response) {
        show(fileName, "videos", "Video", response);
    }

    private HttpServletResponse show(String fileName, String directoryName, String type, HttpServletResponse response) {
        File file = new File(directoryName + File.separator + fileName);
        if (!file.exists()) {
            throw new NotFoundException(type + " does not exist!");
        }
        try {
            response.setContentType(Files.probeContentType(file.toPath()));
            Files.copy(file.toPath(), response.getOutputStream());
        } catch (IOException e) {
            throw new BadRequestException(e.getMessage());
        }
        return response;
    }
}
