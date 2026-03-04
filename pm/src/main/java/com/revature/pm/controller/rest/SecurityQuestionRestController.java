package com.revature.pm.controller.rest;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.revature.pm.dto.SecurityQuestionDTO;
import com.revature.pm.entity.SecurityQuestion;
import com.revature.pm.entity.User;
import com.revature.pm.exception.ResourceNotFoundException;
import com.revature.pm.mapper.SecurityQuestionMapper;
import com.revature.pm.repository.SecurityQuestionRepository;
import com.revature.pm.repository.UserRepository;
import com.revature.pm.util.AESUtil;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/security-questions")
public class SecurityQuestionRestController {

	private UserRepository userRepository;
	private SecurityQuestionRepository repository;
	private SecurityQuestionMapper mapper;
	private PasswordEncoder passwordEncoder;

	public SecurityQuestionRestController(UserRepository userRepository, SecurityQuestionRepository repository,
			SecurityQuestionMapper mapper, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.repository = repository;
		this.mapper = mapper;
		this.passwordEncoder = passwordEncoder;
	}

	@GetMapping
	public ResponseEntity<List<SecurityQuestionDTO>> getUserQuestions(Authentication authentication) {

		String username = authentication.getName();

		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));

		List<SecurityQuestionDTO> dtos = repository.findByUser(user).stream().map(mapper::toDTO).toList();

		return ResponseEntity.ok(dtos);
	}

	@PutMapping
	public ResponseEntity<String> updateAnswers(@Valid @RequestBody List<SecurityQuestionDTO> dtos,
			@RequestParam String masterPassword, Authentication authentication) {

		String username = authentication.getName();

		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));

		// Verify master password using BCrypt
		if (!passwordEncoder.matches(masterPassword, user.getMasterPassword())) {
			return ResponseEntity.badRequest().body("Invalid master password");
		}

		List<SecurityQuestion> questions = repository.findByUser(user);

		if (dtos.size() < 3) {
			return ResponseEntity.badRequest().body("Minimum 3 security questions required");
		}

		for (int i = 0; i < questions.size(); i++) {

			SecurityQuestion question = questions.get(i);

			String encryptedAnswer = AESUtil.encrypt(dtos.get(i).getAnswer());

			question.setEncryptedAnswer(encryptedAnswer);
		}

		repository.saveAll(questions);

		return ResponseEntity.ok("Security questions updated");
	}
}
