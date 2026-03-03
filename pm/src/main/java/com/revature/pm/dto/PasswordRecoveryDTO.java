package com.revature.pm.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class PasswordRecoveryDTO {

	@NotBlank(message = "Username or Email is required")
	private String usernameOrEmail;

	@NotBlank(message = "New password is required")
	@Size(min = 8, max = 15, message = "New password must be at least 8 and below 15 characters")
	private String newPassword;

	@NotEmpty(message = "Security answers are required")
	@Size(min = 3, message = "All security questions must be answered")
	@Valid
	private List<SecurityQuestionDTO> securityAnswers;

	public PasswordRecoveryDTO() {
	}

	public String getUsernameOrEmail() {
		return usernameOrEmail;
	}

	public void setUsernameOrEmail(String usernameOrEmail) {
		this.usernameOrEmail = usernameOrEmail;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public List<SecurityQuestionDTO> getSecurityAnswers() {
		return securityAnswers;
	}

	public void setSecurityAnswers(List<SecurityQuestionDTO> securityAnswers) {
		this.securityAnswers = securityAnswers;
	}

}