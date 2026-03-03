package com.revature.pm.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PasswordEntryDTO {

	private Long id;

	@NotBlank(message = "Account name is required")
	@Size(max = 100, message = "Account name too long")
	private String accountName;

	@NotBlank(message = "Website URL is required")
	@Size(max = 255, message = "Website URL too long")
	private String websiteUrl;

	@NotBlank(message = "Login username is required")
	@Size(max = 100, message = "Login username too long")
	private String loginUsername;

	@NotBlank(message = "Password is required")
	@Size(min = 8, max = 64, message = "Password must be at least 8 and below 64 characters")
	private String password;

	@Size(max = 50, message = "Category too long")
	private String category;

	@Size(max = 500, message = "Notes too long")
	private String notes;

	private boolean favorite;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	public PasswordEntryDTO() {
		// required for jackon deserialization
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getWebsiteUrl() {
		return websiteUrl;
	}

	public void setWebsiteUrl(String websiteUrl) {
		this.websiteUrl = websiteUrl;
	}

	public String getLoginUsername() {
		return loginUsername;
	}

	public void setLoginUsername(String loginUsername) {
		this.loginUsername = loginUsername;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public boolean isFavorite() {
		return favorite;
	}

	public void setFavorite(boolean favorite) {
		this.favorite = favorite;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

}