package com.tiktok.controller;

import com.tiktok.model.dto.messageDTO.MessageDTO;
import com.tiktok.model.dto.messageDTO.SendMessageRequestDTO;
import com.tiktok.model.dto.messageDTO.SendMessageResponseDTO;
import com.tiktok.model.dto.userDTO.ResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
public class MessageController extends GlobalController {

    @PostMapping("/users/{receiver_id}/messages")
    public ResponseEntity<SendMessageResponseDTO> sendMessage(@PathVariable (value = "receiver_id")int rid,
                                                              @Valid @RequestBody SendMessageRequestDTO dto,
                                                              HttpServletRequest req) {
        SendMessageResponseDTO responseDTO = messageService.sendMessage(rid, dto.getText(), getUserIdFromSession(req));
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    @PutMapping("/users/messages/{mid}")
    public ResponseEntity<SendMessageResponseDTO> editMessage(@PathVariable int mid,
                                                              @Valid @RequestBody SendMessageRequestDTO dto,
                                                              HttpServletRequest req) {
        SendMessageResponseDTO responseDTO = messageService.editMessage(mid, dto.getText(), getUserIdFromSession(req));
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    @DeleteMapping("/messages/{mid}")
    public ResponseEntity<ResponseDTO> deleteMessage(@PathVariable int mid,
                                                     HttpServletRequest req) {
        messageService.delete(mid, getUserIdFromSession(req));
        return new ResponseEntity<>(getResponseDTO("You deleted a message."), HttpStatus.OK);
    }

    @PostMapping("/users/sub/messages")
    public ResponseEntity<ResponseDTO> sendMessageToSub(@Valid @RequestBody SendMessageRequestDTO dto,
                                                        HttpServletRequest req) {
        messageService.sendMessageSub(dto.getText(), getUserIdFromSession(req));
        return new ResponseEntity<>(getResponseDTO("The message" + dto.getText() + "was successfully sent to all your subscribers."), HttpStatus.OK);
    }

    @GetMapping("/users/{receiver_id}/messages")
    public ResponseEntity<List<MessageDTO>> messagesWithUser(@PathVariable(value = "receiver_id") int rid,
                                                             @RequestParam(value = "page", defaultValue = "0") int page,
                                                             @RequestParam(value = "perPage", defaultValue = "10") int perPage,
                                                             HttpServletRequest req) {
        return new ResponseEntity<>(messageService.messagesWithUser(rid, getUserIdFromSession(req), page, perPage), HttpStatus.OK);
    }


}
