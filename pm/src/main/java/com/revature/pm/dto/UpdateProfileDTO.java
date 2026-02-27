package com.revature.pm.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class UpdateProfileDTO {

	@NotBlank(message = "Email is required")
	@Email(message = "Invalid email format")
	private String email;
	@Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be 10 digits")
	private String phoneNumber;

	public UpdateProfileDTO() {
		// required for jackon deserialization
	}

	public UpdateProfileDTO(String email, String phoneNumber) {
		this.email = email;
		this.phoneNumber = phoneNumber;
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
}