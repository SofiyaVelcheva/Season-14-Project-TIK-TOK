package com.tiktok.model.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ErrorDTO {

    private int status;
    private LocalDate localDate;
    private String msg;


}
