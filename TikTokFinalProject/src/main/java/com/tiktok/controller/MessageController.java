package com.tiktok.controller;

import com.tiktok.model.dto.message.MessageDTO;
import com.tiktok.model.dto.message.SendMessageRequestDTO;
import com.tiktok.model.dto.message.SendMessageResponseDTO;
import com.tiktok.model.dto.user.ResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
public class MessageController extends GlobalController {

    @PostMapping("/users/{receiver_id}/messages")
    public ResponseEntity<SendMessageResponseDTO> sendMessage(@PathVariable(value = "receiver_id") int rid,
                                                              @Valid @RequestBody SendMessageRequestDTO dto,
                                                              HttpServletRequest req) {
        SendMessageResponseDTO responseDTO = messageService.sendMessage(rid, dto.getText(), getUserIdFromSession(req));
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    @PutMapping("/users/messages/{messageId}")
    public ResponseEntity<SendMessageResponseDTO> editMessage(@PathVariable int messageId,
                                                              @Valid @RequestBody SendMessageRequestDTO dto,
                                                              HttpServletRequest req) {
        SendMessageResponseDTO responseDTO = messageService.editMessage(messageId, dto.getText(), getUserIdFromSession(req));
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    @DeleteMapping("/messages/{messageId}")
    public ResponseEntity<ResponseDTO> deleteMessage(@PathVariable int messageId,
                                                     HttpServletRequest req) {
        messageService.delete(messageId, getUserIdFromSession(req));
        return new ResponseEntity<>(getResponseDTO("You deleted a message."), HttpStatus.OK);
    }

    @PostMapping("/users/sub/messages")
    public ResponseEntity<ResponseDTO> sendMessageToSub(@Valid @RequestBody SendMessageRequestDTO dto,
                                                        HttpServletRequest req) {
        messageService.sendMessageSub(dto.getText(), getUserIdFromSession(req));
        return new ResponseEntity<>(getResponseDTO("The message < " + dto.getText() + " > was successfully sent to all your subscribers."), HttpStatus.OK);
    }


    @GetMapping("/users/{user_id}/messages")
    public ResponseEntity<List<MessageDTO>> messagesWithUser(@PathVariable(value = "user_id") int userId,
                                                             @RequestParam(value = "page", defaultValue = "0") int page,
                                                             @RequestParam(value = "perPage", defaultValue = "10") int perPage,
                                                             HttpServletRequest req) {
        return new ResponseEntity<>(messageService.messagesWithUser(userId, getUserIdFromSession(req), page, perPage), HttpStatus.OK);
    }

}
