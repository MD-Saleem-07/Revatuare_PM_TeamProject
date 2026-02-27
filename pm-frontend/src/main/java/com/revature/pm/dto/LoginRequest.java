package com.revature.pm.dto;

public class LoginRequest {

	private String usernameOrEmail;
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
