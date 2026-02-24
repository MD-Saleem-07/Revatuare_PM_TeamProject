package com.revature.pm.controller.rest;

import com.revature.pm.dto.*;
import com.revature.pm.security.JwtUtil;
import com.revature.pm.service.AuthService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthRestController {

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private AuthService authService;

	// Register
	@PostMapping("/register")
	public ResponseEntity<String> register(@RequestBody RegistrationDTO dto) {
		authService.registerUser(dto);
		return ResponseEntity.ok("User registered successfully");
	}

	@GetMapping("/questions")
	public ResponseEntity<List<String>> getSecurityQuestions() {
		return ResponseEntity.ok(authService.getPredefinedQuestions());
	}

	@PostMapping("/login")
	public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginDTO loginDTO) {

		LoginResponseDTO response = authService.login(loginDTO);

		return ResponseEntity.ok(response);
	}

	// Change Master Password
	@PutMapping("/{userId}/change-password")
	public ResponseEntity<String> changePassword(@PathVariable Long userId, @RequestBody ChangePasswordDTO dto) {

		authService.changeMasterPassword(userId, dto);
		return ResponseEntity.ok("Password changed successfully");
	}

	// Recover Password
	@PostMapping("/recover")
	public ResponseEntity<String> recoverPassword(@RequestBody PasswordRecoveryDTO dto) {

		authService.recoverMasterPassword(dto);
		return ResponseEntity.ok("Password recovered successfully");
	}

	@GetMapping("/recover/questions")
	public ResponseEntity<List<String>> getRecoveryQuestions(@RequestParam String usernameOrEmail) {

		return ResponseEntity.ok(authService.getUserSecurityQuestions(usernameOrEmail));
	}

	@PostMapping("/verify-otp")
	public ResponseEntity<LoginResponseDTO> verifyOtp(@RequestParam String username, @RequestParam String otp) {

		authService.verifyCode(username, otp);

		String token = jwtUtil.generateToken(username);

		return ResponseEntity.ok(new LoginResponseDTO("SUCCESS", token));
	}
}