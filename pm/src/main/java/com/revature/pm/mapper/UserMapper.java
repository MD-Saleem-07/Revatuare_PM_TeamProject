package com.revature.pm.mapper;

import com.revature.pm.dto.RegistrationDTO;
import com.revature.pm.entity.User;

public class UserMapper {

    public static User toEntity(RegistrationDTO dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        return user;
    }
}