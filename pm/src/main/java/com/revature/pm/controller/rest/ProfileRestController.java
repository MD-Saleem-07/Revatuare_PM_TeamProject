package com.revature.pm.controller.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//import com.revature.pm.dto.UpdateProfileDTO;
import com.revature.pm.dto.UserProfileDTO;
import com.revature.pm.entity.User;
import com.revature.pm.exception.ResourceNotFoundException;
import com.revature.pm.repository.UserRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/profile")
public class ProfileRestController {

	private UserRepository userRepository;

	public ProfileRestController(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

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

	@PutMapping
	public ResponseEntity<String> updateProfile(@Valid @RequestBody UpdateProfileDTO dto,
			Authentication authentication) {

		String username = authentication.getName();

		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));

		user.setEmail(dto.getEmail());
		user.setPhoneNumber(dto.getPhoneNumber());

		userRepository.save(user);

		return ResponseEntity.ok("Profile updated successfully");
	}
}