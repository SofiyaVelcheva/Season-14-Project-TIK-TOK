package com.tiktok.service;

import com.tiktok.model.dto.userDTO.LoginRequestDTO;
import com.tiktok.model.dto.userDTO.UserLoginResponseDTO;
import com.tiktok.model.entities.User;
import com.tiktok.model.exceptions.BadRequestException;
import com.tiktok.model.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    public UserLoginResponseDTO login(LoginRequestDTO user) {

        Optional<User> u = userRepository.
                findByUsernameAndPassword(user.getUsername(), user.getPassword());

        if (!u.isPresent()){
            throw new BadRequestException("Username or password invalid!");
        }

        return modelMapper.map(u.get(), UserLoginResponseDTO.class);

    }
}
