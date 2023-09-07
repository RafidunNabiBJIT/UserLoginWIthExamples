package com.spring.jwt.controller;

import com.spring.jwt.entity.UserEntity;
import com.spring.jwt.model.UserDto;
import com.spring.jwt.model.UserRegistrationResponse;
import com.spring.jwt.repository.UserRepository;
import com.spring.jwt.service.UserService;
import com.spring.jwt.utils.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @GetMapping("/hello")
    public String hello(){
        return "Hello";
    }

    @GetMapping("/hello2")
    public String hello2(){
        return "Hello2";
    }
    @PostMapping("/registration")
    public ResponseEntity<?> register (@RequestBody UserDto userDto) {
        try{
            UserDto createdUser = userService.createUser(userDto);
            String accessToken = JWTUtils.generateToken(createdUser.getEmail());
            UserRegistrationResponse response = new UserRegistrationResponse(createdUser, accessToken);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }catch(Exception e)
        {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }

    }
}

