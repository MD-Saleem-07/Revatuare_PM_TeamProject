package com.revature.pm.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import org.apache.catalina.User;

@Entity
@Table(name = "password_entries")
public class PasswordEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String accountName;

    @Column(nullable = false)
    private String websiteUrl;

    @Column(nullable = false)
    private String loginUsername;

    @Column(nullable = false)
    private String encryptedPassword;   // Will store AES encrypted password

    @Column
    private String category;   // Social, Banking, Email, etc.

    @Column(length = 1000)
    private String notes;

    @Column
    private boolean favorite;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

   
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public PasswordEntry() {
    }

    public PasswordEntry(Long id, String accountName, String websiteUrl,
                         String loginUsername, String encryptedPassword,
                         String category, String notes,
                         boolean favorite,
                         LocalDateTime createdAt,
                         LocalDateTime updatedAt,
                         User user) {
        this.id = id;
        this.accountName = accountName;
        this.websiteUrl = websiteUrl;
        this.loginUsername = loginUsername;
        this.encryptedPassword = encryptedPassword;
        this.category = category;
        this.notes = notes;
        this.favorite = favorite;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.user = user;
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

	public String getEncryptedPassword() {
		return encryptedPassword;
	}

	public void setEncryptedPassword(String encryptedPassword) {
		this.encryptedPassword = encryptedPassword;
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

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

    
}