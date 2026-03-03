package com.revature.pm.mapper;

import org.springframework.stereotype.Component;
import com.revature.pm.entity.User;
import com.revature.pm.dto.UserDTO;

@Component
public class UserMapper {

	public UserDTO toDTO(User user) {
		if (user == null)
			return null;

		return new UserDTO(user.getId(), user.getUsername(), user.getEmail(), user.getPhoneNumber(),
				user.isTwoFactorEnabled());
	}

	public User toEntity(UserDTO dto) {
		if (dto == null)
			return null;

		User user = new User();
		user.setId(dto.getId());
		user.setUsername(dto.getUsername());
		user.setEmail(dto.getEmail());
		user.setPhoneNumber(dto.getPhoneNumber());
		user.setTwoFactorEnabled(dto.isTwoFactorEnabled());
		return user;
	}
}