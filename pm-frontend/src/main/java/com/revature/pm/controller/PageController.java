package com.revature.pm.controller;

import jakarta.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.revature.pm.dto.AuditReportDTO;
import com.revature.pm.dto.ChangePasswordDTO;
import com.revature.pm.dto.DashboardStatsDTO;
import com.revature.pm.dto.PageResponse;
import com.revature.pm.dto.PasswordEntryDTO;
import com.revature.pm.dto.PasswordGenerationRequestDTO;
import com.revature.pm.dto.SecurityQuestionDTO;
import com.revature.pm.dto.UpdateProfileDTO;
import com.revature.pm.dto.UserProfileDTO;
import com.revature.pm.dto.VerificationCodeDTO;
import com.revature.pm.dto.ViewPasswordDTO;



@Controller
public class PageController {

	private RestTemplate restTemplate;

	public PageController(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	@GetMapping("/")
	public String home() {
		return "redirect:/login";
	}

	@GetMapping("/dashboard")
	public String dashboardPage(HttpSession session, Model model) {

		String token = (String) session.getAttribute("JWT_TOKEN");

		if (token == null) {
			return "redirect:/login";
		}

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + token);

		HttpEntity<String> entity = new HttpEntity<>(headers);

		ResponseEntity<DashboardStatsDTO> response = restTemplate.exchange("http://localhost:8080/api/vault/dashboard",
				HttpMethod.GET, entity, DashboardStatsDTO.class);

		model.addAttribute("stats", response.getBody());

		return "dashboard";
	}

	@GetMapping("/vault")
	public String vault(@RequestParam(defaultValue = "0") int page, HttpSession session, Model model) {

		String token = (String) session.getAttribute("JWT_TOKEN");

		if (token == null) {
			return "redirect:/login";
		}

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + token);

		HttpEntity<String> entity = new HttpEntity<>(headers);

		ResponseEntity<PageResponse<PasswordEntryDTO>> response = restTemplate.exchange(
				"http://localhost:8080/api/vault?page=" + page + "&size=7", HttpMethod.GET, entity,
				new ParameterizedTypeReference<PageResponse<PasswordEntryDTO>>() {
				});

		PageResponse<PasswordEntryDTO> pageData = response.getBody();

		model.addAttribute("passwords", pageData.getContent());
		model.addAttribute("currentPage", pageData.getNumber());
		model.addAttribute("totalPages", pageData.getTotalPages());

		return "vault";
	}

	@GetMapping("/vault/add")
	public String addPasswordPage(HttpSession session) {
		if (session.getAttribute("JWT_TOKEN") == null) {
			return "redirect:/login";
		}
		return "add-password";
	}

	@PostMapping(value = "/vault/add", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public String addPasswordFromForm(@ModelAttribute PasswordEntryDTO dto, HttpSession session) {

		String token = (String) session.getAttribute("JWT_TOKEN");

		if (token == null) {
			return "redirect:/login";
		}

		sendToBackend(dto, token);

		return "redirect:/vault";
	}

	// For Generator (JSON)
	@PostMapping(value = "/vault/add", consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<String> addPasswordFromJson(@RequestBody PasswordEntryDTO dto, HttpSession session) {

		String token = (String) session.getAttribute("JWT_TOKEN");

		if (token == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Session expired");
		}

		sendToBackend(dto, token);

		return ResponseEntity.ok("Saved Successfully");
	}

	private void sendToBackend(PasswordEntryDTO dto, String token) {

		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(token);
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<PasswordEntryDTO> entity = new HttpEntity<>(dto, headers);

		restTemplate.exchange("http://localhost:8080/api/vault", HttpMethod.POST, entity, String.class);
	}

	@GetMapping("/vault/delete/{id}")
	public String deletePassword(@PathVariable Long id, HttpSession session) {

		String token = (String) session.getAttribute("JWT_TOKEN");

		if (token == null) {
			return "redirect:/login";
		}

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + token);

		HttpEntity<String> entity = new HttpEntity<>(headers);

		restTemplate.exchange("http://localhost:8080/api/vault/" + id, HttpMethod.DELETE, entity, String.class);

		return "redirect:/vault";
	}

	@PostMapping("/vault/favorite/{id}")
	@ResponseBody
	public ResponseEntity<Void> toggleFavorite(@PathVariable Long id, HttpSession session) {

		String token = (String) session.getAttribute("JWT_TOKEN");

		if (token == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + token);

		HttpEntity<String> entity = new HttpEntity<>(headers);

		restTemplate.exchange("http://localhost:8080/api/vault/" + id + "/favorite", HttpMethod.PUT, entity,
				String.class);

		return ResponseEntity.ok().build();
	}

	@GetMapping("/vault/favorites")
	public String viewFavorites(HttpSession session, Model model) {

		String token = (String) session.getAttribute("JWT_TOKEN");

		if (token == null) {
			return "redirect:/login";
		}

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + token);

		HttpEntity<Void> entity = new HttpEntity<>(headers);

		ResponseEntity<PasswordEntryDTO[]> response = restTemplate.exchange("http://localhost:8080/api/vault/favorites",
				HttpMethod.GET, entity, PasswordEntryDTO[].class);

		model.addAttribute("passwords", response.getBody());
		model.addAttribute("isFavoritesView", true);

		return "vault";
	}

	@GetMapping("/vault/edit/{id}")
	public String editPasswordPage(@PathVariable Long id, @RequestParam(defaultValue = "0") int page,
			HttpSession session, Model model) {

		String token = (String) session.getAttribute("JWT_TOKEN");

		if (token == null) {
			return "redirect:/login";
		}

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + token);

		HttpEntity<String> entity = new HttpEntity<>(headers);

		ResponseEntity<PageResponse<PasswordEntryDTO>> response = restTemplate.exchange(
				"http://localhost:8080/api/vault?page=" + page + "&size=7", HttpMethod.GET, entity,
				new ParameterizedTypeReference<PageResponse<PasswordEntryDTO>>() {
				});

		PageResponse<PasswordEntryDTO> pageData = response.getBody();

		PasswordEntryDTO selected = null;

		for (PasswordEntryDTO dto : pageData.getContent()) {
			if (dto.getId().equals(id)) {
				selected = dto;
				break;
			}
		}

		model.addAttribute("password", selected);

		return "edit-password";
	}

	@PostMapping("/vault/edit")
	public String updatePassword(@ModelAttribute PasswordEntryDTO dto, HttpSession session) {

		String token = (String) session.getAttribute("JWT_TOKEN");

		if (token == null) {
			return "redirect:/login";
		}

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + token);
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<PasswordEntryDTO> entity = new HttpEntity<>(dto, headers);

		restTemplate.exchange("http://localhost:8080/api/vault/" + dto.getId(), HttpMethod.PUT, entity, String.class);

		return "redirect:/vault";
	}

	@GetMapping("/vault/search")
	public String searchPasswords(@RequestParam String keyword, HttpSession session, Model model) {

		String token = (String) session.getAttribute("JWT_TOKEN");
		if (token == null)
			return "redirect:/login";

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + token);

		HttpEntity<String> entity = new HttpEntity<>(headers);

		ResponseEntity<PasswordEntryDTO[]> response = restTemplate.exchange(
				"http://localhost:8080/api/vault/search?keyword=" + keyword, HttpMethod.GET, entity,
				PasswordEntryDTO[].class);

		model.addAttribute("passwords", response.getBody());

		return "vault";
	}

	@GetMapping("/vault/filter")
	public String filterPasswords(@RequestParam String category, HttpSession session, Model model) {

		String token = (String) session.getAttribute("JWT_TOKEN");
		if (token == null)
			return "redirect:/login";

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + token);

		HttpEntity<String> entity = new HttpEntity<>(headers);

		ResponseEntity<PasswordEntryDTO[]> response = restTemplate.exchange(
				"http://localhost:8080/api/vault/filter?category=" + category, HttpMethod.GET, entity,
				PasswordEntryDTO[].class);

		model.addAttribute("passwords", response.getBody());

		return "vault";
	}

	@GetMapping("/vault/sort")
	public String sortPasswords(@RequestParam String sortBy, HttpSession session, Model model) {

		String token = (String) session.getAttribute("JWT_TOKEN");
		if (token == null)
			return "redirect:/login";

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + token);

		HttpEntity<String> entity = new HttpEntity<>(headers);

		ResponseEntity<PasswordEntryDTO[]> response = restTemplate.exchange(
				"http://localhost:8080/api/vault/sort?sortBy=" + sortBy, HttpMethod.GET, entity,
				PasswordEntryDTO[].class);

		model.addAttribute("passwords", response.getBody());

		return "vault";
	}

	@PostMapping("/vault/view/{id}")
	@ResponseBody
	public ResponseEntity<String> viewPassword(@PathVariable Long id, @RequestBody ViewPasswordDTO dto,
			HttpSession session) {

		String token = (String) session.getAttribute("JWT_TOKEN");

		if (token == null) {
			return ResponseEntity.status(401).build();
		}

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + token);
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<ViewPasswordDTO> entity = new HttpEntity<>(dto, headers);

		try {

			ResponseEntity<String> response = restTemplate.exchange("http://localhost:8080/api/vault/" + id + "/view",
					HttpMethod.POST, entity, String.class);

			return ResponseEntity.ok(response.getBody());

		} catch (Exception e) {

			return ResponseEntity.badRequest().build();
		}
	}

	@PostMapping("/generate-operation-otp")
	@ResponseBody
	public ResponseEntity<?> generateOperationOtp(HttpSession session) {

		String token = (String) session.getAttribute("JWT_TOKEN");

		if (token == null) {
			return ResponseEntity.status(401).body("Unauthorized");
		}

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + token);

		HttpEntity<Void> entity = new HttpEntity<>(null, headers);

		try {

			ResponseEntity<VerificationCodeDTO> response = restTemplate.exchange(
					"http://localhost:8080/api/auth/operation-otp", HttpMethod.POST, entity, VerificationCodeDTO.class);

			return ResponseEntity.ok(response.getBody());

		} catch (org.springframework.web.client.HttpClientErrorException e) {

			return ResponseEntity.badRequest().body("ENABLE_2FA_REQUIRED");
		} catch (Exception e) {

			return ResponseEntity.status(500).body("OTP_GENERATION_FAILED");
		}
	}

	@PostMapping("/verify-operation-otp")
	@ResponseBody
	public ResponseEntity<String> verifyOperationOtp(@RequestParam String otp, HttpSession session) {

		String token = (String) session.getAttribute("JWT_TOKEN");

		if (token == null) {
			return ResponseEntity.status(401).body("Unauthorized");
		}

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + token);

		HttpEntity<Void> entity = new HttpEntity<>(null, headers);

		try {

			restTemplate.exchange("http://localhost:8080/api/auth/verify-operation-otp?otp=" + otp, HttpMethod.POST,
					entity, String.class);

			return ResponseEntity.ok("Verified");

		} catch (Exception e) {
			return ResponseEntity.badRequest().body("Invalid OTP");
		}
	}

	@PostMapping("/generator/generate")
	@ResponseBody
	public ResponseEntity<String> generatePassword(@RequestBody PasswordGenerationRequestDTO request,
			HttpSession session) {

		String token = (String) session.getAttribute("JWT_TOKEN");

		if (token == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + token);
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<PasswordGenerationRequestDTO> entity = new HttpEntity<>(request, headers);

		ResponseEntity<String> response = restTemplate.exchange("http://localhost:8080/api/generator/generate",
				HttpMethod.POST, entity, String.class);

		return ResponseEntity.ok(response.getBody());
	}

	@PostMapping("/generator/generate-multiple")
	@ResponseBody
	public ResponseEntity<List<String>> generateMultiple(@RequestBody PasswordGenerationRequestDTO request,
			@RequestParam int count, HttpSession session) {

		String token = (String) session.getAttribute("JWT_TOKEN");

		if (token == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + token);
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<PasswordGenerationRequestDTO> entity = new HttpEntity<>(request, headers);

		ResponseEntity<List<String>> response = restTemplate.exchange(
				"http://localhost:8080/api/generator/generate-multiple?count=" + count, HttpMethod.POST, entity,
				new ParameterizedTypeReference<List<String>>() {
				});

		return ResponseEntity.ok(response.getBody());
	}

	@GetMapping("/generator")
	public String generator(HttpSession session) {
		if (session.getAttribute("JWT_TOKEN") == null) {
			return "redirect:/login";
		}
		return "generator";
	}

	@GetMapping("/audit")
	public String audit(HttpSession session, Model model) {

		String token = (String) session.getAttribute("JWT_TOKEN");

		if (token == null) {
			return "redirect:/login";
		}

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + token);

		HttpEntity<String> entity = new HttpEntity<>(headers);

		try {

			ResponseEntity<AuditReportDTO> response = restTemplate.exchange("http://localhost:8080/api/audit",
					HttpMethod.GET, entity, AuditReportDTO.class);

			model.addAttribute("audit", response.getBody());

		} catch (Exception e) {
			model.addAttribute("error", "Unable to generate audit report.");
		}

		return "audit";
	}

	@GetMapping("/profile")
	public String profile(HttpSession session, Model model) {

		try {

			String token = (String) session.getAttribute("JWT_TOKEN");
			if (token == null)
				return "redirect:/login";

			HttpHeaders headers = new HttpHeaders();
			headers.set("Authorization", "Bearer " + token);

			HttpEntity<String> entity = new HttpEntity<>(headers);

			ResponseEntity<UserProfileDTO> response = restTemplate.exchange("http://localhost:8080/api/profile",
					HttpMethod.GET, entity, UserProfileDTO.class);

			UserProfileDTO profile = response.getBody();

			if (profile == null) {
				profile = new UserProfileDTO();
			}
			// System.out.println("profile api rrsponse" + response.getBody());

			model.addAttribute("profile", profile);

			return "profile";

		} catch (Exception e) {

			e.printStackTrace();
			return "redirect:/dashboard";
		}
	}

	@PostMapping("/profile/update")
	public String updateProfile(@RequestParam String email, @RequestParam String phoneNumber, HttpSession session) {

		String token = (String) session.getAttribute("JWT_TOKEN");

		if (token == null) {
			return "redirect:/login";
		}

		try {

			HttpHeaders headers = new HttpHeaders();
			headers.set("Authorization", "Bearer " + token);
			headers.setContentType(MediaType.APPLICATION_JSON);

			UpdateProfileDTO dto = new UpdateProfileDTO(email, phoneNumber);

			HttpEntity<UpdateProfileDTO> entity = new HttpEntity<>(dto, headers);

			restTemplate.exchange("http://localhost:8080/api/profile", HttpMethod.PUT, entity, String.class);

			return "redirect:/profile?success=true";

		} catch (Exception e) {
			return "redirect:/profile?error=true";
		}
	}

	@GetMapping("/security-questions")
	public String securityQuestionsPage(HttpSession session, Model model) {

		String token = (String) session.getAttribute("JWT_TOKEN");

		if (token == null) {
			return "redirect:/login";
		}

		try {

			HttpHeaders headers = new HttpHeaders();
			headers.set("Authorization", "Bearer " + token);

			HttpEntity<String> entity = new HttpEntity<>(headers);

			ResponseEntity<SecurityQuestionDTO[]> response = restTemplate.exchange(
					"http://localhost:8080/api/security-questions", HttpMethod.GET, entity,
					SecurityQuestionDTO[].class);

			model.addAttribute("questions", response.getBody());

			return "security-questions";

		} catch (Exception e) {
			return "redirect:/profile?error=true";
		}
	}

	@PostMapping("/security-questions/update")
	public String updateSecurityQuestions(@RequestParam String masterPassword, @RequestParam List<String> answer,
			HttpSession session) {

		String token = (String) session.getAttribute("JWT_TOKEN");

		if (token == null) {
			return "redirect:/login";
		}

		try {

			HttpHeaders headers = new HttpHeaders();
			headers.set("Authorization", "Bearer " + token);
			headers.setContentType(MediaType.APPLICATION_JSON);

			List<SecurityQuestionDTO> dtos = new ArrayList<>();

			for (String ans : answer) {
				SecurityQuestionDTO dto = new SecurityQuestionDTO();
				dto.setAnswer(ans);
				dtos.add(dto);
			}

			HttpEntity<List<SecurityQuestionDTO>> entity = new HttpEntity<>(dtos, headers);

			restTemplate.exchange("http://localhost:8080/api/security-questions" + "?masterPassword=" + masterPassword,
					HttpMethod.PUT, entity, String.class);

			return "redirect:/security-questions?success=true";

		} catch (Exception e) {
			return "redirect:/security-questions?error=true";
		}
	}

	// GET Questions (Frontend -> Backend)
	@GetMapping("/security-questions/api")
	@ResponseBody
	public ResponseEntity<?> getSecurityQuestions(HttpSession session) {

		String token = (String) session.getAttribute("JWT_TOKEN");

		if (token == null) {
			return ResponseEntity.status(401).body("Unauthorized");
		}

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + token);

		HttpEntity<?> entity = new HttpEntity<>(headers);

		return restTemplate.exchange("http://localhost:8080/api/security-questions", HttpMethod.GET, entity,
				Object.class);
	}

	// UPDATE Questions (Frontend -> Backend)
	@PutMapping("/security-questions/api")
	@ResponseBody
	public ResponseEntity<?> updateSecurityQuestions(@RequestParam String masterPassword, @RequestBody Object body,
			HttpSession session) {

		String token = (String) session.getAttribute("JWT_TOKEN");

		if (token == null) {
			return ResponseEntity.status(401).body("Unauthorized");
		}

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + token);
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<Object> entity = new HttpEntity<>(body, headers);

		return restTemplate.exchange("http://localhost:8080/api/security-questions?masterPassword=" + masterPassword,
				HttpMethod.PUT, entity, String.class);
	}

	@GetMapping("/settings")
	public String settings(HttpSession session, Model model) {

		String token = (String) session.getAttribute("JWT_TOKEN");
		if (token == null)
			return "redirect:/login";

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + token);

		HttpEntity<Void> entity = new HttpEntity<>(headers);

		ResponseEntity<UserProfileDTO> response = restTemplate.exchange("http://localhost:8080/api/profile",
				HttpMethod.GET, entity, UserProfileDTO.class);

		model.addAttribute("profile", response.getBody());

		return "settings";
	}

	@PostMapping("/settings/enable2fa")
	public String enable2FA(HttpSession session) {

		String token = (String) session.getAttribute("JWT_TOKEN");
		if (token == null)
			return "redirect:/login";

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + token);

		HttpEntity<String> entity = new HttpEntity<>(headers);

		restTemplate.exchange("http://localhost:8080/api/settings/2fa/enable", HttpMethod.PUT, entity, String.class);

		return "redirect:/profile";
	}

	@PostMapping("/settings/disable2fa")
	public String disable2FA(HttpSession session) {

		String token = (String) session.getAttribute("JWT_TOKEN");
		if (token == null)
			return "redirect:/login";

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + token);

		HttpEntity<String> entity = new HttpEntity<>(headers);

		restTemplate.exchange("http://localhost:8080/api/settings/2fa/disable", HttpMethod.PUT, entity, String.class);

		return "redirect:/profile";
	}

	@PostMapping("/settings/change-password")
	public String changePassword(@ModelAttribute ChangePasswordDTO dto, HttpSession session) {

		// Confirm password validation
		if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
			return "redirect:/settings?error=true";
		}

		try {

			String token = (String) session.getAttribute("JWT_TOKEN");
			if (token == null)
				return "redirect:/login";

			HttpHeaders headers = new HttpHeaders();
			headers.set("Authorization", "Bearer " + token);
			headers.setContentType(MediaType.APPLICATION_JSON);

			// sending only curr and new pwd
			ChangePasswordDTO backendDto = new ChangePasswordDTO();
			backendDto.setCurrentPassword(dto.getCurrentPassword());
			backendDto.setNewPassword(dto.getNewPassword());

			HttpEntity<ChangePasswordDTO> entity = new HttpEntity<>(backendDto, headers);

			restTemplate.exchange("http://localhost:8080/api/settings/change-password", HttpMethod.PUT, entity,
					String.class);

			return "redirect:/settings?success=true";

		} catch (Exception e) {

			return "redirect:/settings?error=true";
		}
	}

	@GetMapping("/backup")
	public String backupPage(HttpSession session) {

		if (session.getAttribute("JWT_TOKEN") == null) {
			return "redirect:/login";
		}

		return "backup";
	}

	@GetMapping("/backup/export")
	public ResponseEntity<byte[]> exportVault(HttpSession session) {

		String token = (String) session.getAttribute("JWT_TOKEN");

		if (token == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + token);

		HttpEntity<String> entity = new HttpEntity<>(headers);

		ResponseEntity<String> response = restTemplate.exchange("http://localhost:8080/api/backup/export",
				HttpMethod.GET, entity, String.class);

		String encryptedData = response.getBody();

		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=vault-backup.txt")
				.body(encryptedData.getBytes());
	}

	@PostMapping("/backup/import")
	public String importVault(@RequestParam("file") MultipartFile file, HttpSession session) {

		try {

			String token = (String) session.getAttribute("JWT_TOKEN");

			if (token == null) {
				return "redirect:/login";
			}

			String encryptedBackup = new String(file.getBytes());

			HttpHeaders headers = new HttpHeaders();
			headers.set("Authorization", "Bearer " + token);
			headers.setContentType(MediaType.APPLICATION_JSON);

			HttpEntity<String> entity = new HttpEntity<>(encryptedBackup, headers);

			restTemplate.exchange("http://localhost:8080/api/backup/import", HttpMethod.POST, entity, String.class);

			return "redirect:/backup?success=true";

		} catch (Exception e) {

			return "redirect:/backup?error=true";
		}
	}
}