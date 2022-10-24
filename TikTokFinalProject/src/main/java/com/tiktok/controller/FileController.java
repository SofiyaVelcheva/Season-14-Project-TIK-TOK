package com.tiktok.controller;


import com.tiktok.model.exceptions.NotFoundException;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.nio.file.Files;

@RestController
public class FileController extends GlobalController {

    @GetMapping("/image/{fileName}")
    @SneakyThrows
    public void getProfilePhoto(@PathVariable String fileName, HttpServletResponse resp) {
    File f = new File("photo" + File.separator + fileName);
    if (!f.exists()){
        throw new NotFoundException("File does not exist!");
    }
    resp.setContentType(Files.probeContentType(f.toPath()));
    Files.copy(f.toPath(), resp.getOutputStream());
    }
}
