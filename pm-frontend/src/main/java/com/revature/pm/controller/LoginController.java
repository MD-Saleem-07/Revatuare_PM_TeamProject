package com.revature.pm.controller;


import jakarta.servlet.http.HttpSession;

import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.revature.pm.dto.LoginRequest;
import com.revature.pm.dto.LoginResponseDTO;
import com.revature.pm.dto.PasswordRecoveryRequest;
import com.revature.pm.dto.RegistrationRequest;
import com.revature.pm.dto.VerificationCodeDTO;

@Controller
public class LoginController {

	private RestTemplate restTemplate;

	public LoginController(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	private static final String BACKEND_BASE_URL = "http://localhost:8080/api/auth";
	private static final String BACKEND_REGISTER_URL = BACKEND_BASE_URL + "/register";

	@GetMapping("/login")
	public String loginPage() {
		return "auth/login";
	}

	@PostMapping("/login")
	public String handleLogin(@ModelAttribute LoginRequest request, HttpSession session) {

		try {

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			HttpEntity<LoginRequest> entity = new HttpEntity<>(request, headers);

			ResponseEntity<LoginResponseDTO> response = restTemplate.exchange("http://localhost:8080/api/auth/login",
					HttpMethod.POST, entity, LoginResponseDTO.class);

			LoginResponseDTO body = response.getBody();

			// Normal Login Success
			if (body.getStatus().equals("SUCCESS")) {

				session.setAttribute("JWT_TOKEN", body.getToken());
				String usernameOrEmail = request.getUsernameOrEmail();

				String displayName;

				if (usernameOrEmail.contains("@")) {
					displayName = usernameOrEmail.substring(0, usernameOrEmail.indexOf("@"));
				} else {
					displayName = usernameOrEmail;
				}

				session.setAttribute("USERNAME", displayName);

				return "redirect:/dashboard";
			}

			// otp Required
			if (body.getStatus().equals("OTP_REQUIRED")) {

				session.setAttribute("TEMP_USERNAME", request.getUsernameOrEmail());

				// to generate OTP
				HttpHeaders headers2 = new HttpHeaders();
				headers2.setContentType(MediaType.APPLICATION_JSON);

				HttpEntity<String> entity2 = new HttpEntity<>(headers2);

				ResponseEntity<VerificationCodeDTO> otpResponse = restTemplate.exchange(
						"http://localhost:8080/api/auth/generate-otp?username=" + request.getUsernameOrEmail(),
						HttpMethod.POST, entity2, VerificationCodeDTO.class);

				session.setAttribute("LOGIN_OTP", otpResponse.getBody().getCode());

				return "redirect:/login?otpRequired=true";
			}

			return "redirect:/login?error=true";

		} catch (Exception e) {
			return "redirect:/login?error=true";
		}
	}

	@PostMapping("/verify-otp")
	public String verifyOtp(@RequestParam String otp, HttpSession session) {

		String username = (String) session.getAttribute("TEMP_USERNAME");

		if (username == null) {
			return "redirect:/login";
		}

		try {

			String url = "http://localhost:8080/api/auth/verify-otp" + "?username=" + username + "&otp=" + otp;

			ResponseEntity<LoginResponseDTO> response = restTemplate.postForEntity(url, null, LoginResponseDTO.class);

			LoginResponseDTO body = response.getBody();

			if (body != null && "SUCCESS".equals(body.getStatus())) {

				session.setAttribute("JWT_TOKEN", body.getToken());

				session.setAttribute("USERNAME", username);

				session.removeAttribute("LOGIN_OTP");
				session.removeAttribute("TEMP_USERNAME");

				return "redirect:/dashboard";
			}

			return "redirect:/login?otpRequired=true&otpError=true";

		} catch (Exception e) {

			return "redirect:/login?otpRequired=true&otpError=true";
		}
	}

	@PostMapping("/resend-otp")
	public String resendOtp(HttpSession session) {

		String username = (String) session.getAttribute("TEMP_USERNAME");

		if (username == null) {
			return "redirect:/login";
		}

		try {

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			HttpEntity<String> entity = new HttpEntity<>(headers);

			ResponseEntity<VerificationCodeDTO> response = restTemplate.exchange(
					"http://localhost:8080/api/auth/generate-otp?username=" + username, HttpMethod.POST, entity,
					VerificationCodeDTO.class);

			session.setAttribute("LOGIN_OTP", response.getBody().getCode());

			return "redirect:/login?otpRequired=true";

		} catch (Exception e) {
			return "redirect:/login?error=true";
		}
	}

	@GetMapping("/register")
	public String registerPage() {
		return "auth/register";
	}

	@PostMapping("/register")
	public String handleRegister(@ModelAttribute RegistrationRequest request) {

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<RegistrationRequest> entity = new HttpEntity<>(request, headers);

		try {

			restTemplate.exchange(BACKEND_REGISTER_URL, HttpMethod.POST, entity, String.class);

			return "redirect:/login?registered=true";

		} catch (Exception e) {
			return "redirect:/register?error=true";
		}
	}

	@GetMapping("/recover")
	public String recoverPage() {
		return "auth/recover";
	}

	@PostMapping("/recover")
	public String processRecovery(@ModelAttribute PasswordRecoveryRequest request,
			RedirectAttributes redirectAttributes) {

		try {

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			HttpEntity<PasswordRecoveryRequest> entity = new HttpEntity<>(request, headers);

			restTemplate.postForEntity("http://localhost:8080/api/auth/recover", entity, String.class);

			redirectAttributes.addFlashAttribute("successMessage", "Password recovered successfully!");

			return "redirect:/login?recovered=true";

		} catch (Exception e) {

			redirectAttributes.addFlashAttribute("errorMessage",
					"Recovery attempt failed. Please verify your answers.");

			return "redirect:/recover";
		}
	}

	@GetMapping("/logout")
	public String logout(HttpSession session) {

		session.invalidate();

		return "redirect:/login";
	}
}