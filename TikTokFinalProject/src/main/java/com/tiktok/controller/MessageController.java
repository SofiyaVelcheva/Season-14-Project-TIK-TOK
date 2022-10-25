package com.tiktok.controller;

import com.tiktok.model.dto.messageDTO.MessageResponseDTO;
import com.tiktok.model.dto.messageDTO.SendMessageRequestDTO;
import com.tiktok.model.dto.messageDTO.SendMessageResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
public class MessageController extends GlobalController{

    @PostMapping("/messages/{rid}")
    public ResponseEntity<SendMessageResponseDTO> sendMessage(@PathVariable int rid,
                                                              @Valid @RequestBody SendMessageRequestDTO dto,
                                                              HttpServletRequest req){
        SendMessageResponseDTO responseDTO = messageService.sendMessage(rid, dto.getText(), getUserIdFromSession(req));
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    @PutMapping("/messages/{mid}")
    public ResponseEntity<SendMessageResponseDTO> editMessage(@PathVariable int mid,
                                                              @Valid @RequestBody SendMessageRequestDTO dto,
                                                              HttpServletRequest req){
        SendMessageResponseDTO responseDTO = messageService.editMessage(mid, dto.getText(), getUserIdFromSession(req));
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    @DeleteMapping("/messages/{mid}")
    public ResponseEntity<String> deleteMessage(@PathVariable int mid, HttpServletRequest req){
        messageService.delete(mid, getUserIdFromSession(req));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("messages/sub")
    public ResponseEntity<String> sendMessageSub(@Valid @RequestBody SendMessageRequestDTO dto,
                                                 HttpServletRequest req){
        messageService.sendMessageSub(dto.getText(), getUserIdFromSession(req));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("messages/{rid}")
    public ResponseEntity<MessageResponseDTO> messagesWithUser(@PathVariable int rid,
                                                                     HttpServletRequest req){
        return new ResponseEntity<>(messageService.messagesWithUser(rid, getUserIdFromSession(req)), HttpStatus.OK);
    }



}
