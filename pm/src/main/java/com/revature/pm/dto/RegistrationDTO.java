package com.revature.pm.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RegistrationDTO {

	@NotBlank(message = "Username is required")
	@Size(min = 3, max = 30, message = "Username must be between 3 and 30 characters")
	private String username;

	@NotBlank(message = "Email is required")
	@Email(message = "Invalid email format")
	private String email;

	@NotBlank(message = "Phone number is required")
	@Pattern(regexp = "^[6-9]\\d{9}$", message = "Phone number must be 10 digits and start with 6, 7, 8, or 9")
	private String phoneNumber;

	@NotBlank(message = "Master password is required")
	@Size(min = 8, max = 15, message = "matser password must be at least 8 and below 15 characters")
	private String masterPassword;

	@NotEmpty(message = "Exactly 3 security questions must be selected")
	@Size(min = 3, max = 3, message = "Exactly 3 security questions must be selected")
	@Valid
	private List<SecurityQuestionDTO> securityQuestions;

	public RegistrationDTO() {
	}

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

	public String getMasterPassword() {
		return masterPassword;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setMasterPassword(String masterPassword) {
		this.masterPassword = masterPassword;
	}

	public List<SecurityQuestionDTO> getSecurityQuestions() {
		return securityQuestions;
	}

	public void setSecurityQuestions(List<SecurityQuestionDTO> securityQuestions) {
		this.securityQuestions = securityQuestions;
	}

}