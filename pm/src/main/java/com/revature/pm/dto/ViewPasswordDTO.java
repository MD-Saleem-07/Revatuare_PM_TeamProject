package com.revature.pm.dto;

import jakarta.validation.constraints.NotBlank;

public class ViewPasswordDTO {

	@NotBlank(message = "Master password is required")
	private String masterPassword;

	public ViewPasswordDTO() {
	}

	public ViewPasswordDTO(String masterPassword) {
		this.masterPassword = masterPassword;
	}

	public String getMasterPassword() {
		return masterPassword;
	}

	public void setMasterPassword(String masterPassword) {
		this.masterPassword = masterPassword;
	}
}