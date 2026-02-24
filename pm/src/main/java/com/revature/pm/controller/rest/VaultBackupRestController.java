package com.revature.pm.controller;

import com.revature.pm.service.VaultBackupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/backup")
public class VaultBackupRestController {

	@Autowired
	private VaultBackupService vaultBackupService;

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