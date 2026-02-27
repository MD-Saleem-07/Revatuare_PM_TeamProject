package com.revature.pm.dto;

public class ViewPasswordDTO {

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
