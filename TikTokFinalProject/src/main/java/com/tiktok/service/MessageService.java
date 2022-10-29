package com.tiktok.service;

import com.tiktok.model.dto.messageDTO.MessageDTO;
import com.tiktok.model.dto.messageDTO.SendMessageResponseDTO;
import com.tiktok.model.dto.userDTO.PublisherUserDTO;
import com.tiktok.model.entities.Message;
import com.tiktok.model.entities.User;
import com.tiktok.model.exceptions.BadRequestException;
import com.tiktok.model.exceptions.UnauthorizedException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;


@Service
public class MessageService extends GlobalService {
    public SendMessageResponseDTO sendMessage(int rid, String text, int userId) {
        if (rid == userId){
            throw new BadRequestException("Message cannot be sent because sender and receiver are the same user.");
        }
        User sender = getUserById(userId);
        User receiver = getUserById(rid);
        if (!sender.getSubscribeTo().contains(receiver)) {
            throw new BadRequestException("Message cannot be sent because you aren't following this user.");
        }
        Message message = createMessage(text, sender, receiver);
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
            createMessage(text, sender, subscriber);
        }
    }

    public List<MessageDTO> messagesWithUser(int rid, int userId, int page, int perPage) {
        if (!getUserById(userId).getSubscribers().contains(getUserById(rid)) ||
                !getUserById(userId).getSubscribeTo().contains(getUserById(rid))) {
            throw new BadRequestException("Unknown user.");
        }
        Pageable pageable = PageRequest.of(page, perPage);
        List<Message> messages = messageRepository.correspondence(rid, userId, pageable);
        checkCollection(messages);
        return messages.stream().map(m -> {
            MessageDTO messageDTO = modelMapper.map(m, MessageDTO.class);
            messageDTO.setSender(modelMapper.map(m.getSender(), PublisherUserDTO.class));
            return messageDTO;
        }).toList();
    }

    private Message createMessage(String text, User sender, User receiver) {
        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setText(text);
        message.setSend_at(LocalDateTime.now());
        messageRepository.save(message);
        return message;
    }

    private void checkOwner(int userId, Message message) {
        if (message.getSender().getId() != userId) {
            throw new UnauthorizedException("Sorry, you are not the owner of the message.");
        }
    }
}
