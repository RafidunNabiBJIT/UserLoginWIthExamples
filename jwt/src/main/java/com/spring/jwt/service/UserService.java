package com.spring.jwt.service;


import com.spring.jwt.model.UserDto;

public interface UserService {
    UserDto createUser(UserDto user) throws Exception;
    UserDto getUser(String email);

    UserDto getUserByUserId(String id) throws Exception;

    boolean userExistsWithEmail(String email);
}