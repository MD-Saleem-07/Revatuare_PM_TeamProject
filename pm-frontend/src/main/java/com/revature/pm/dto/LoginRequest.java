package com.revature.pm.dto;

import jakarta.validation.constraints.NotBlank;

public class LoginRequest {

	@NotBlank(message="Username or Email is required")
	private String usernameOrEmail;
	
	@NotBlank(message="Master password is required")
	private String masterPassword;

	public LoginRequest() {
	}

	public LoginRequest(String usernameOrEmail, String masterPassword) {
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
