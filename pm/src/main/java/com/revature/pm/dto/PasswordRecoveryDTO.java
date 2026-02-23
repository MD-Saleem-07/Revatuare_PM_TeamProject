package com.revature.pm.dto;

import java.util.List;

public class PasswordRecoveryDTO {

	private String usernameOrEmail;
	private String newPassword;
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