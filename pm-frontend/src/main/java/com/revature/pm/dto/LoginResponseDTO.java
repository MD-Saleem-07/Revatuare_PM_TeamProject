package com.revature.pm.dto;

public class LoginResponseDTO {

	private String status;
	private String token;

	public LoginResponseDTO() {
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}
