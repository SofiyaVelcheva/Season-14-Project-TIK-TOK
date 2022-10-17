package com.tiktok.controller;

import com.tiktok.model.dto.errorDTO.ErrorDTO;
import com.tiktok.model.exceptions.BadRequestException;
import com.tiktok.model.exceptions.NotFoundException;
import com.tiktok.model.exceptions.UnauthorizedException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDateTime;

public abstract class GlobalController {

    @Autowired
    public ModelMapper modelMapper;

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

    private ErrorDTO getErrorDTO(Exception e, HttpStatus status) {
        e.printStackTrace();
        ErrorDTO dto = new ErrorDTO();
        dto.setMsg(e.getMessage());
        dto.setTime(LocalDateTime.now());
        dto.setStatus(status.value());
        return dto;
    }


}
