package com.revature.pm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class VerificationCodeDTO {

	@NotBlank(message = "Verification code is required")
	@Pattern(regexp = "^\\d{6}$", message = "Verification code must be exactly 6 digits")
	private String code;

	public VerificationCodeDTO() {
	}

	public VerificationCodeDTO(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

}