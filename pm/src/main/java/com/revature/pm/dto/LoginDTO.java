package com.revature.pm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class LoginDTO {

	@NotBlank(message = "Username or Email is required")
	private String usernameOrEmail;

	@NotBlank(message = "Password is required")
	@Size(min = 8, max = 15, message = "Password must be at least 8 and below 15 characters")
	private String masterPassword;

	public LoginDTO() {
	}

	public LoginDTO(String usernameOrEmail, String masterPassword) {
		this.usernameOrEmail = usernameOrEmail;
		this.masterPassword = masterPassword;
	}

	public String getUsernameOrEmail() {
		return usernameOrEmail;
	}

	public void setUsernameOrEmail(String usernameOrEmail) {
		this.usernameOrEmail = usernameOrEmail;
	}

	public String getMasterPassword() {
		return masterPassword;
	}

	public void setMasterPassword(String masterPassword) {
		this.masterPassword = masterPassword;
	}
}