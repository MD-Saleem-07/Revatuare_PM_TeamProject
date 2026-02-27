package com.revature.pm.dto;

public class UserProfileDTO {

	private String username;
	private String email;
	public String phoneNumber;
	private boolean twoFactorEnabled;

	public String getUsername() {
		return username;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String setPhoneNumber() {
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

	public boolean isTwoFactorEnabled() {
		return twoFactorEnabled;
	}

	public void setTwoFactorEnabled(boolean twoFactorEnabled) {
		this.twoFactorEnabled = twoFactorEnabled;
	}

}
