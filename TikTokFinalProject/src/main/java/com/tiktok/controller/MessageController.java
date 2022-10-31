package com.tiktok.controller;

import com.tiktok.model.dto.TextResponseDTO;
import com.tiktok.model.dto.message.MessageDTO;
import com.tiktok.model.dto.message.SendMessageRequestDTO;
import com.tiktok.model.dto.message.SendMessageResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
public class MessageController extends GlobalController {

    @PostMapping("/users/{receiverId}/messages")
    public ResponseEntity<SendMessageResponseDTO> sendMessage(@PathVariable(value = "receiverId") int rid,
                                                              @Valid @RequestBody SendMessageRequestDTO dto,
                                                              HttpServletRequest request) {
        SendMessageResponseDTO responseDTO = messageService.sendMessage(rid, dto.getText(), getUserIdFromSession(request));
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    @PutMapping("/users/messages/{messageId}")
    public ResponseEntity<SendMessageResponseDTO> editMessage(@PathVariable int messageId,
                                                              @Valid @RequestBody SendMessageRequestDTO dto,
                                                              HttpServletRequest request) {
        SendMessageResponseDTO responseDTO = messageService.editMessage(messageId, dto.getText(), getUserIdFromSession(request));
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    @DeleteMapping("/messages/{messageId}")
    public ResponseEntity<TextResponseDTO> deleteMessage(@PathVariable int messageId,
                                                         HttpServletRequest request) {
        messageService.delete(messageId, getUserIdFromSession(request));
        return new ResponseEntity<>(getResponseDTO("You deleted a message."), HttpStatus.OK);
    }

    @PostMapping("/users/sub/messages")
    public ResponseEntity<TextResponseDTO> sendMessageToSub(@Valid @RequestBody SendMessageRequestDTO dto,
                                                            HttpServletRequest request) {
        messageService.sendMessageSub(dto.getText(), getUserIdFromSession(request));
        return new ResponseEntity<>(getResponseDTO("The message < " + dto.getText() + " > was successfully sent to all your subscribers."), HttpStatus.OK);
    }


    @GetMapping("/users/{userId}/messages")
    public ResponseEntity<List<MessageDTO>> messagesWithUser(@PathVariable(value = "userId") int userId,
                                                             @RequestParam(value = "page", defaultValue = "0") int page,
                                                             @RequestParam(value = "perPage", defaultValue = "10") int perPage,
                                                             HttpServletRequest request) {
        return new ResponseEntity<>(messageService.messagesWithUser(userId, getUserIdFromSession(request), page, perPage), HttpStatus.OK);
    }

}
