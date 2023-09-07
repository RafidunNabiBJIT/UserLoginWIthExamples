package com.spring.jwt.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.jwt.SpringApplicationContext;
import com.spring.jwt.constants.AppConstants;
import com.spring.jwt.model.UserDto;
import com.spring.jwt.model.UserLoginRequestModel;
import com.spring.jwt.service.UserService;
import com.spring.jwt.utils.JWTUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    @Autowired
    private final AuthenticationManager authenticationManager;
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        try {
            UserLoginRequestModel creds = new ObjectMapper().readValue(request.getInputStream(), UserLoginRequestModel.class);
            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(creds.getEmail(),creds.getPassword())
            );
        } catch (IOException e) {
            log.info("Exception occured at attemptAuthentication method: {}",e.getLocalizedMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {

        String user = ((User) authResult.getPrincipal()).getUsername();
        String accessToken = JWTUtils.generateToken(user);

        UserService userService = (UserService) SpringApplicationContext.getBean("userServiceImpl");
        UserDto userDto = userService.getUser(user);

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("userId", userDto.getUserId());
        responseBody.put(AppConstants.HEADER_STRING, AppConstants.TOKEN_PREFIX + accessToken);

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse = objectMapper.writeValueAsString(responseBody);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(jsonResponse);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        try {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);


            String errorMessage = "Authentication failed: ";

            if (failed instanceof BadCredentialsException) {
                errorMessage += "Invalid password.";
            } else if (failed instanceof UsernameNotFoundException) {
                errorMessage += "Invalid email.";
            } else {
                errorMessage += failed.getMessage();
            }
            response.setContentType("application/json");

            response.getWriter().write("{\"error\": \"" + errorMessage + "\"}");
        } catch (IOException e) {

            e.printStackTrace();
        }
    }








}
