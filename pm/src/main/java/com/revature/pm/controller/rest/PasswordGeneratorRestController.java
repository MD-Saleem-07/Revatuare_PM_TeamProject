package com.revature.pm.controller.rest;

import com.revature.pm.dto.PasswordGenerationRequestDTO;
import com.revature.pm.service.PasswordGeneratorService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/generator")
public class PasswordGeneratorRestController {

	@Autowired
	private PasswordGeneratorService passwordGeneratorService;

	// Generate single password
	@PostMapping("/generate")
	public ResponseEntity<String> generatePassword(@RequestBody PasswordGenerationRequestDTO dto) {

		String password = passwordGeneratorService.generateSinglePassword(dto.getLength(), dto.isIncludeUpper(),
				dto.isIncludeLower(), dto.isIncludeNumbers(), dto.isIncludeSpecial(), dto.isExcludeSimilar());

		return ResponseEntity.ok(password);
	}

	// Generate multiple passwords
	@PostMapping("/generate-multiple")
	public ResponseEntity<List<String>> generateMultiple(@RequestBody PasswordGenerationRequestDTO dto,
			@RequestParam int count) {

		List<String> passwords = passwordGeneratorService.generateMultiplePasswords(count, dto.getLength(),
				dto.isIncludeUpper(), dto.isIncludeLower(), dto.isIncludeNumbers(), dto.isIncludeSpecial(),
				dto.isExcludeSimilar());

		return ResponseEntity.ok(passwords);
	}

	// Check strength
	@GetMapping("/strength")
	public ResponseEntity<String> checkStrength(@RequestParam String password) {

		return ResponseEntity.ok(passwordGeneratorService.getPasswordStrength(password));
	}
}