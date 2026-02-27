package com.revature.pm.dto;

import java.util.List;

public class RegistrationRequest {

	private String username;
	private String email;
	private String phoneNumber;
	private String masterPassword;
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
