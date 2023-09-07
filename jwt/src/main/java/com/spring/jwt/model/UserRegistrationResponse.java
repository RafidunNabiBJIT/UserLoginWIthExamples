package com.spring.jwt.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
public class UserRegistrationResponse {
    private UserDto userDto;
    private String token;

    public UserRegistrationResponse(UserDto userDto, String token) {
        this.userDto = userDto;
        this.token = token;
    }

}
