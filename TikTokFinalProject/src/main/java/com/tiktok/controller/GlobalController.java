package com.tiktok.controller;

import com.tiktok.model.dto.TextResponseDTO;
import com.tiktok.model.dto.error.ErrorDTO;
import com.tiktok.model.exceptions.BadRequestException;
import com.tiktok.model.exceptions.NotFoundException;
import com.tiktok.model.exceptions.UnauthorizedException;
import com.tiktok.service.*;
import com.tiktok.service.CommentService;
import com.tiktok.service.UserService;
import com.tiktok.service.MessageService;
import com.tiktok.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public abstract class GlobalController {

    private static final String LOGGED = "logged";
    private static final String USER_ID = "userId";
    private static final String REMOTE_IP = "remoteIp";

    @Autowired
    public HashtagService hashtagService;
    @Autowired
    public MessageService messageService;
    @Autowired
    public UserService userService;
    @Autowired
    public VideoService videoService;
    @Autowired
    public CommentService commentService;
    @Autowired
    public FileService fileService;

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDTO handleNotFound(Exception e) {
        return getErrorDTO(e, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDTO handleBadRequest(Exception e) {
        return getErrorDTO(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorDTO handleUnauthorized(Exception e) {
        return getErrorDTO(e, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorDTO handleAllOthers(Exception e) {
        return getErrorDTO(e, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

    private ErrorDTO getErrorDTO(Exception e, HttpStatus status) {
        e.printStackTrace();
        ErrorDTO dto = new ErrorDTO();
        dto.setMsg(e.getMessage());
        dto.setTime(LocalDateTime.now());
        dto.setStatus(status.value());
        return dto;
    }

    public int getUserIdFromSession(HttpServletRequest request) {
        HttpSession session = request.getSession();
        if (!isLogged(request)) {
            session.invalidate();
            throw new UnauthorizedException("You have to log in!");
        }
        int userId = (int) session.getAttribute(USER_ID);
        if (!userService.verifyAccount(userId)) {
            throw new UnauthorizedException("You should verify your email!");
        }
        session.setMaxInactiveInterval(30 * 60 * 1000); // 30 minutes
        return userId;
    }

    public boolean isLogged(HttpServletRequest request) {
        HttpSession session = request.getSession();
        return Boolean.TRUE.equals(session.getAttribute(LOGGED))
                && session.getAttribute(USER_ID) != null
                && session.getAttribute(REMOTE_IP).equals(request.getRemoteAddr());
    }

    public void setSession(HttpServletRequest request, int id) {
        HttpSession session = request.getSession();
        session.setAttribute(LOGGED, Boolean.TRUE);
        session.setAttribute(USER_ID, id);
        session.setAttribute(REMOTE_IP, request.getRemoteAddr());
    }

    public TextResponseDTO getResponseDTO(String text) {
        TextResponseDTO dto = new TextResponseDTO();
        dto.setMessage(text);
        return dto;
    }

}

