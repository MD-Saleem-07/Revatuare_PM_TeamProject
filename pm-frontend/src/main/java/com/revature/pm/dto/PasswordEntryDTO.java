package com.revature.pm.dto;

import java.time.LocalDateTime;

public class PasswordEntryDTO {

	private Long id;
	private String accountName;
	private String websiteUrl;
	private String loginUsername;
	private String password;
	private String category;
	private String notes;
	private boolean favorite;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	public PasswordEntryDTO() {
	}

	public Long getId() {
		return id;
	}

	public String getAccountName() {
		return accountName;
	}

	public String getWebsiteUrl() {
		return websiteUrl;
	}

	public String getLoginUsername() {
		return loginUsername;
	}

	public String getPassword() {
		return password;
	}

	public String getCategory() {
		return category;
	}

	public String getNotes() {
		return notes;
	}

	public boolean isFavorite() {
		return favorite;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public void setWebsiteUrl(String websiteUrl) {
		this.websiteUrl = websiteUrl;
	}

	public void setLoginUsername(String loginUsername) {
		this.loginUsername = loginUsername;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public void setFavorite(boolean favorite) {
		this.favorite = favorite;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}
}
