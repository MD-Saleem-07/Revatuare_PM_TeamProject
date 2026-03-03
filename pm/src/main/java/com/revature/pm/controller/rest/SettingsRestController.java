package com.revature.pm.controller.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.revature.pm.dto.ChangePasswordDTO;
import com.revature.pm.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/settings")
public class SettingsRestController {

	private AuthService authService;

	public SettingsRestController(AuthService authService) {
		this.authService = authService;
	}

	@PutMapping("/2fa/enable")
	public ResponseEntity<String> enable2FA(Authentication authentication) {

		authService.enableTwoFactorByUsername(authentication.getName());

		return ResponseEntity.ok("2FA Enabled");
	}

	@PutMapping("/2fa/disable")
	public ResponseEntity<String> disable2FA(Authentication authentication) {

		authService.disableTwoFactorByUsername(authentication.getName());

		return ResponseEntity.ok("2FA Disabled");
	}

	@PutMapping("/change-password")
	public ResponseEntity<String> changePassword(Authentication authentication,
			@Valid @RequestBody ChangePasswordDTO dto) {

		authService.changeMasterPasswordByUsername(authentication.getName(), dto);

		return ResponseEntity.ok("Password Changed Successfully");
	}
}