package com.tiktok.model.dto.messageDTO;

import com.tiktok.model.dto.userDTO.PublisherUserDTO;
import lombok.Data;

import java.util.List;

@Data
public class MessageResponseDTO {
    private PublisherUserDTO receiver;
    private List<MessageDTO> messages;

}
