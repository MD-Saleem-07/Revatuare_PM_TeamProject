package com.revature.pm.controller.rest;

import com.revature.pm.dto.DashboardStatsDTO;
import com.revature.pm.dto.PasswordEntryDTO;
import com.revature.pm.dto.ViewPasswordDTO;
import com.revature.pm.service.PasswordEntryService;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vault")
public class PasswordEntryRestController {

	private PasswordEntryService passwordEntryService;

	public PasswordEntryRestController(PasswordEntryService passwordEntryService) {

		this.passwordEntryService = passwordEntryService;
	}

	// Add Password
	@PostMapping
	public ResponseEntity<String> addPassword(Authentication authentication, @RequestBody PasswordEntryDTO dto) {

		String username = authentication.getName();
		passwordEntryService.addPasswordByUsername(username, dto);

		return ResponseEntity.ok("Password added successfully");
	}

	@GetMapping("/dashboard")
	public ResponseEntity<DashboardStatsDTO> getDashboardStats(Authentication authentication) {

		String username = authentication.getName();

		return ResponseEntity.ok(passwordEntryService.getDashboardStatsByUsername(username));
	}

	@GetMapping
	public ResponseEntity<Page<PasswordEntryDTO>> getVaultPasswords(Authentication authentication,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

		String username = authentication.getName();

		Page<PasswordEntryDTO> passwordPage = passwordEntryService.getPasswordsPageByUsername(username, page, size);

		return ResponseEntity.ok(passwordPage);
	}

	// Update Password
	@PutMapping("/{entryId}")
	public ResponseEntity<String> updatePassword(Authentication authentication, @PathVariable Long entryId,
			@RequestBody PasswordEntryDTO dto) {

		String username = authentication.getName();

		passwordEntryService.updatePasswordByUsername(username, entryId, dto);

		return ResponseEntity.ok("Password updated successfully");
	}

	// Delete Password
	@DeleteMapping("/{entryId}")
	public ResponseEntity<String> deletePassword(Authentication authentication, @PathVariable Long entryId) {

		String username = authentication.getName();

		passwordEntryService.deletePasswordByUsername(username, entryId);

		return ResponseEntity.ok("Password deleted successfully");
	}

	// Toggle Favorite
	@PutMapping("/{entryId}/favorite")
	public ResponseEntity<String> toggleFavorite(Authentication authentication, @PathVariable Long entryId) {

		String username = authentication.getName();

		passwordEntryService.toggleFavoriteByUsername(username, entryId);

		return ResponseEntity.ok("Favorite status updated");
	}

	// Get Favorites
	@GetMapping("/favorites")
	public ResponseEntity<List<PasswordEntryDTO>> getFavorites(Authentication authentication) {

		String username = authentication.getName();

		return ResponseEntity.ok(passwordEntryService.getFavoritePasswordsByUsername(username));
	}

	// Search
	@GetMapping("/search")
	public ResponseEntity<List<PasswordEntryDTO>> search(Authentication authentication, @RequestParam String keyword) {

		String username = authentication.getName();

		return ResponseEntity.ok(passwordEntryService.searchPasswordsByUsername(username, keyword));
	}

	// Filter By Category
	@GetMapping("/filter")
	public ResponseEntity<List<PasswordEntryDTO>> filter(Authentication authentication, @RequestParam String category) {

		String username = authentication.getName();

		return ResponseEntity.ok(passwordEntryService.filterByCategoryByUsername(username, category));
	}

	// Sort
	@GetMapping("/sort")
	public ResponseEntity<List<PasswordEntryDTO>> sort(Authentication authentication, @RequestParam String sortBy) {

		String username = authentication.getName();

		return ResponseEntity.ok(passwordEntryService.sortPasswordsByUsername(username, sortBy));
	}

	// View Actual Password
	@PostMapping("/{entryId}/view")
	public ResponseEntity<String> viewPassword(Authentication authentication, @PathVariable Long entryId,
			@RequestBody ViewPasswordDTO dto) {

		String username = authentication.getName();

		String password = passwordEntryService.viewPasswordByUsername(username, entryId, dto);

		return ResponseEntity.ok(password);
	}
}