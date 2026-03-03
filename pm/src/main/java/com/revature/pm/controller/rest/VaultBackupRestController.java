package com.revature.pm.controller.rest;

import com.revature.pm.service.VaultBackupService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/backup")
public class VaultBackupRestController {

	private VaultBackupService vaultBackupService;

	public VaultBackupRestController(VaultBackupService vaultBackupService) {
		this.vaultBackupService = vaultBackupService;
	}

	@GetMapping("/export")
	public ResponseEntity<String> exportVault(Authentication authentication) {

		String username = authentication.getName();

		String encryptedBackup = vaultBackupService.exportVaultByUsername(username);

		return ResponseEntity.ok(encryptedBackup);
	}

	@PostMapping("/import")
	public ResponseEntity<String> importVault(Authentication authentication, @RequestBody String encryptedBackup) {

		String username = authentication.getName();

		vaultBackupService.importVaultByUsername(username, encryptedBackup);

		return ResponseEntity.ok("Vault imported successfully");
	}
}