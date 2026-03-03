package com.revature.pm.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

public class RegistrationRequest {

	@NotBlank(message = "Username is required")
	private String username;

	@NotBlank(message = "Email is required")
	@Email(message = "Invalid email format")
	private String email;

	@NotBlank(message = "Phone number is required")
	@Pattern(regexp = "^[6-9]\\d{9}$", message = "Phone number must be 10 digits and start with 6, 7, 8 or 9")
	private String phoneNumber;

	@NotBlank(message = "Master password is required")
	@Size(min = 8, max = 15, message = "Password must be at least 8 and below 15 characters long")
	private String masterPassword;

	@Valid
	private List<SecurityQuestionRequest> securityQuestions;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getMasterPassword() {
		return masterPassword;
	}

	public void setMasterPassword(String masterPassword) {
		this.masterPassword = masterPassword;
	}

	public List<SecurityQuestionRequest> getSecurityQuestions() {
		return securityQuestions;
	}

	public void setSecurityQuestions(List<SecurityQuestionRequest> securityQuestions) {
		this.securityQuestions = securityQuestions;
	}

}
