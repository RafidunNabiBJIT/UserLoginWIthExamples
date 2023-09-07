package com.spring.jwt.service.impl;

import com.spring.jwt.entity.UserEntity;
import com.spring.jwt.model.UserDto;
import com.spring.jwt.repository.UserRepository;
import com.spring.jwt.service.UserService;
import com.spring.jwt.utils.JWTUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService, UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Override
    public UserDto createUser(UserDto user) throws Exception {
        ModelMapper modelMapper = new ModelMapper();
        if (userRepository.findByEmail(user.getEmail()).isPresent())
            throw new Exception("Record already exists");
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(user.getEmail());
        userEntity.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        String publicUserId = JWTUtils.generateUserID(10);
        userEntity.setUserId(publicUserId);
        UserEntity storedUserDetails =userRepository.save(userEntity);
        UserDto returnedValue = modelMapper.map(storedUserDetails,UserDto.class);
        return returnedValue;
    }

    @Override
    public UserDto getUser(String email) {
        UserEntity userEntity = userRepository.findByEmail(email).get();
        if(userEntity == null) throw new UsernameNotFoundException("No record found");
        UserDto returnValue = new UserDto();
        BeanUtils.copyProperties(userEntity,returnValue);
        return returnValue;
    }

    @Override
    public UserDto getUserByUserId(String userId) throws Exception {
        UserDto returnValue = new UserDto();
        UserEntity userEntity = userRepository.findByUserId(userId).orElseThrow(Exception::new);
        BeanUtils.copyProperties(userEntity,returnValue);
        return returnValue;
    }
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(email).get();
        if(userEntity==null) throw new UsernameNotFoundException(email);
        return new User(userEntity.getEmail(),userEntity.getPassword(),
                true,true,true,true,new ArrayList<>());
    }

    @Override
    public boolean userExistsWithEmail(String email) {
        Optional<UserEntity> existingUser = userRepository.findByEmail(email);
        return existingUser.isPresent();
    }
}