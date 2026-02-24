package com.revature.pm.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.revature.pm.dto.UserProfileDTO;
import com.revature.pm.entity.User;
import com.revature.pm.exception.ResourceNotFoundException;
import com.revature.pm.repository.UserRepository;

@RestController
@RequestMapping("/api/profile")
public class ProfileRestController {

	@Autowired
	private UserRepository userRepository;

	@GetMapping
	public ResponseEntity<UserProfileDTO> getProfile(Authentication authentication) {

		String username = authentication.getName();

		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));

		UserProfileDTO dto = new UserProfileDTO();
		dto.setUsername(user.getUsername());
		dto.setEmail(user.getEmail());
		dto.setPhoneNumber(user.getPhoneNumber());
		dto.setTwoFactorEnabled(user.isTwoFactorEnabled());

		return ResponseEntity.ok(dto);
	}
}