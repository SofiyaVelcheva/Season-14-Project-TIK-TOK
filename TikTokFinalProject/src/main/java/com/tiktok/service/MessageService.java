package com.tiktok.service;

import com.tiktok.model.dto.messageDTO.MessageDTO;
import com.tiktok.model.dto.messageDTO.MessageResponseDTO;
import com.tiktok.model.dto.messageDTO.SendMessageRequestDTO;
import com.tiktok.model.dto.messageDTO.SendMessageResponseDTO;
import com.tiktok.model.dto.userDTO.PublisherUserDTO;
import com.tiktok.model.entities.Message;
import com.tiktok.model.entities.User;
import com.tiktok.model.exceptions.BadRequestException;
import com.tiktok.model.exceptions.UnauthorizedException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageService extends GlobalService {
    public SendMessageResponseDTO sendMessage(int rid, String text, int userId) {
        User sender = getUserById(userId);
        User receiver = getUserById(rid);
        if (!sender.getSubscribeTo().contains(receiver)) {
            throw new BadRequestException("Message cannot be sent because you aren't following this user.");
        }
        if (!sender.getSubscribers().contains(receiver)) {
            throw new BadRequestException("Message cannot be sent since this user isn't following you.");
        }
        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setText(text);
        message.setSend_at(LocalDateTime.now());
        messageRepository.save(message);
        SendMessageResponseDTO responseDTO = modelMapper.map(message, SendMessageResponseDTO.class);
        responseDTO.setReceiver(modelMapper.map(receiver, PublisherUserDTO.class));
        return responseDTO;
    }

    public SendMessageResponseDTO editMessage(int mid, String newText, int userId) {
        Message message = getMessageById(mid);
        checkOwner(userId, message);
        message.setText(newText);
        message.setSend_at(LocalDateTime.now());
        messageRepository.save(message);
        return modelMapper.map(message, SendMessageResponseDTO.class);
    }

    public void delete(int mid, int userId) {
        Message message = getMessageById(mid);
        checkOwner(userId, message);
        message.setText("Deleted on " + LocalDateTime.now());
        messageRepository.save(message);
    }

    public void sendMessageSub(String text, int userId) {
        User sender = getUserById(userId);
        List<User> allSubscribers = sender.getSubscribers();
        checkCollection(allSubscribers);
        for (User subscriber : allSubscribers) {
            Message message = new Message();
            message.setSender(sender);
            message.setReceiver(subscriber);
            message.setText(text);
            message.setSend_at(LocalDateTime.now());
            messageRepository.save(message);
        }
    }

    public MessageResponseDTO messagesWithUser(int rid, int userId) {
        if (!getUserById(userId).getSubscribers().contains(getUserById(rid)) ||
                !getUserById(userId).getSubscribeTo().contains(getUserById(rid))){
            throw new BadRequestException("Unknown user.");
        }
        List<Message> messages = messageRepository.correspondence(rid, userId);
        checkCollection(messages);
        List<MessageDTO> messageDTOS = messages.stream().map(m -> modelMapper.map(m, MessageDTO.class)).toList();
        MessageResponseDTO responseDTO = new MessageResponseDTO();
        responseDTO.setReceiver(modelMapper.map(getUserById(rid), PublisherUserDTO.class));
        responseDTO.setMessages(messageDTOS);
        return responseDTO;
    }

    private void checkOwner(int userId, Message message) {
        if (message.getSender().getId() != userId) {
            throw new UnauthorizedException("Sorry, you are not the owner of the message.");
        }
    }
}
